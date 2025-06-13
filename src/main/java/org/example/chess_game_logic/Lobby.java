package org.example.chess_game_logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.example.chess_game_logic.chess_pieces.ChessMoveType;
import org.example.chess_game_logic.chess_pieces.Color;
import org.example.chess_game_logic.entities.*;
import org.example.chess_game_logic.requests.MovePieceRequest;
import org.example.chess_game_logic.requests.PromotePieceRequest;
import org.example.chess_game_logic.timer.TimerService;
import org.example.exceptions.*;
import org.example.websocket.WebSocketController;

import java.util.Map;
import java.util.Objects;

public class Lobby {

    @Getter private final Long idLobby;
    @Getter private final Long idPlayer1;
    @Getter private final Long idPlayer2;
    @Getter private final MoveValidator moveValidator;

    private final TimerService timerService;
    private final GameResultEntityRepository gameResultEntityRepository;
    private final ChessMoveRepository chessMoveRepository;
    private final ChessGameRepository chessGameRepository;

    private final ChessGame chessGame;

    private volatile Long currentPlayerId;
    private volatile boolean needsPromotionInfo = false;
    @Setter private volatile boolean stillPlaying = true;

    public Lobby(Long idGame,
                 Long idPlayer1,
                 Long idPlayer2,
                 MoveValidator moveValidator,
                 int nrMinutes,
                 TimerService timerService,
                 ChessMoveRepository chessMoveRepository,
                 ChessGameRepository chessGameRepository,
                 GameResultEntityRepository gameResultEntityRepository) {

        this.idLobby = idGame;
        this.idPlayer1 = idPlayer1;
        this.idPlayer2 = idPlayer2;
        this.moveValidator = moveValidator;
        this.timerService = timerService;
        this.chessMoveRepository = chessMoveRepository;
        this.chessGameRepository = chessGameRepository;
        this.gameResultEntityRepository = gameResultEntityRepository;

        this.chessGame = chessGameRepository.save(new ChessGame(idPlayer1, idPlayer2));
        this.currentPlayerId = idPlayer1;

        timerService.startTimer(idPlayer1, nrMinutes * 60, () -> onTimeout(idPlayer1));
        timerService.startTimer(idPlayer2, nrMinutes * 60, () -> onTimeout(idPlayer2));
        timerService.pauseTimer(idPlayer2);
    }

    public synchronized void processMove(MovePieceRequest request) throws JsonProcessingException {
        if (!stillPlaying) throw new GameOverException(ErrorMessage.GameOver.get());

        try {
            if (needsPromotionInfo) throw new MovePieceException("Server can't respond to this request now!");
            if (!Objects.equals(currentPlayerId, request.getIdPlayer())) throw new MovePieceException("Wait your turn");

            ChessMoveType moveType = getMoveType(request);
            if (moveType == ChessMoveType.WrongMove) throw new MovePieceException("Incorrect move");

            Color playerColor = currentPlayerId.equals(idPlayer1) ? Color.White : Color.Black;
            moveValidator.processMoveRequest(request, moveType, playerColor);

            // Save the move once per move (under moving player's match)
            chessMoveRepository.save(new ChessMove(
null,
                    chessGame.getId(),
                    currentPlayerId,
                    moveValidator.getBoard().getFullmoveNumber(),
                    moveValidator.getBoard().getRealFen(playerColor)
            ));

            switchPlayer();
        } catch (PromInfoNeededException e) {
            needsPromotionInfo = true;
            throw e;
        } catch (GameOverException e) {
            stillPlaying = false;
            timerService.stopTimer(idPlayer1);
            timerService.stopTimer(idPlayer2);

            saveGame(e.getMessage());
            throw e;
        } catch (RunOutOfTimeException e) {
            stillPlaying = false;
            timerService.stopTimer(idPlayer1);
            timerService.stopTimer(idPlayer2);
            saveGame(ErrorMessage.RunOutOfTimeCurrentPlayer.get());
            throw e;
        }
    }

    public void processPromoteRequest(PromotePieceRequest request) {
        if (!needsPromotionInfo) throw new RuntimeException("You can't promote!");
        if (!Objects.equals(request.getIdPlayer(), currentPlayerId)) throw new RuntimeException("Wait your turn!");

        moveValidator.promote(request);
        needsPromotionInfo = false;
        switchPlayer();
    }

    public GameResult forfeit(Long idPlayer) throws Exception {
        if (!stillPlaying) throw new Exception(ErrorMessage.GameOver.get());

        stillPlaying = false;
        timerService.stopTimer(idPlayer1);
        timerService.stopTimer(idPlayer2);
        saveGame(ErrorMessage.Forfeit.get());

        return new GameResult("Win", "Player resigned!");
    }

    private void onTimeout(Long playerId) {
        if (!stillPlaying) return;

        stillPlaying = false;
        timerService.stopTimer(idPlayer1);
        timerService.stopTimer(idPlayer2);

        saveGame(ErrorMessage.RunOutOfTimeCurrentPlayer.get());

        Long opponent = playerId.equals(idPlayer1) ? idPlayer2 : idPlayer1;
        WebSocketController controller = timerService.getWebSocketController();

        controller.sendGameOverMessage(opponent, Map.of(
                "matchResult", "Win",
                "reason", ErrorMessage.RunOutOfTimeOpponentPlayer.get()
        ));
        controller.sendGameOverMessage(playerId, Map.of(
                "matchResult", "Lose",
                "reason", ErrorMessage.RunOutOfTimeCurrentPlayer.get()
        ));
    }

    private void switchPlayer() {
        Long otherPlayer = currentPlayerId.equals(idPlayer1) ? idPlayer2 : idPlayer1;
        timerService.pauseTimer(currentPlayerId);
        currentPlayerId = otherPlayer;
        timerService.resumeTimer(currentPlayerId);
    }

    private ChessMoveType getMoveType(MovePieceRequest request) {
        int xC = request.getXCurrent(), yC = request.getYCurrent();
        int xD = request.getXDestination(), yD = request.getYDestination();

        if (xC == xD && yC == yD) return ChessMoveType.WrongMove;
        if (xC == xD) return ChessMoveType.Horizontal;
        if (yC == yD) return ChessMoveType.Vertical;
        if (Math.abs(xC - xD) == Math.abs(yC - yD)) return ChessMoveType.Diagonal;
        if (isKnightMove(xC, yC, xD, yD)) return ChessMoveType.KnightMove;

        return ChessMoveType.WrongMove;
    }

    private boolean isKnightMove(int xC, int yC, int xD, int yD) {
        int dx = Math.abs(xD - xC), dy = Math.abs(yD - yC);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    private void saveGame(String message) {
        GameResultEntity result1 = new GameResultEntity();
        GameResultEntity result2 = new GameResultEntity();

        result1.setGameId(chessGame.getId());
        result1.setPlayerId(idPlayer1);

        result2.setGameId(chessGame.getId());
        result2.setPlayerId(idPlayer2);

        if (message.equals(ErrorMessage.CheckMate.get())) {
            if (currentPlayerId.equals(idPlayer1)) {
                result1.setResult("Loss");
                result1.setReason(ErrorMessage.CheckMate.get());

                result2.setResult("Win");
                result2.setReason(ErrorMessage.CheckMate.get());
            } else {
                result2.setResult("Loss");
                result2.setReason(ErrorMessage.CheckMate.get());

                result1.setResult("Win");
                result1.setReason(ErrorMessage.CheckMate.get());
            }
        }

        if (message.equals(ErrorMessage.RunOutOfTimeCurrentPlayer.get())) {
            if (currentPlayerId.equals(idPlayer1)) {
                result1.setResult("Loss");
                result1.setReason(ErrorMessage.RunOutOfTimeCurrentPlayer.get());

                result2.setResult("Win");
                result2.setReason(ErrorMessage.RunOutOfTimeOpponentPlayer.get());
            } else {
                result2.setResult("Loss");
                result2.setReason(ErrorMessage.RunOutOfTimeCurrentPlayer.get());

                result1.setResult("Win");
                result1.setReason(ErrorMessage.RunOutOfTimeOpponentPlayer.get());
            }
        }

        if (message.equals(ErrorMessage.Forfeit.get())) {
            if (currentPlayerId.equals(idPlayer1)) {
                result1.setResult("Loss");
                result1.setReason("You resigned");

                result2.setResult("Win");
                result2.setReason(ErrorMessage.Forfeit.get());
            } else {
                result2.setResult("Loss");
                result2.setReason("You resigned");

                result1.setResult("Win");
                result1.setReason(ErrorMessage.Forfeit.get());
            }
        }

        gameResultEntityRepository.save(result1);
        gameResultEntityRepository.save(result2);
    }

}
