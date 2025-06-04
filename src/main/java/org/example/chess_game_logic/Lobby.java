package org.example.chess_game_logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.example.chess_game_logic.chess_pieces.ChessMoveType;
import org.example.chess_game_logic.chess_pieces.Color;
import org.example.chess_game_logic.requests.MovePieceRequest;
import org.example.chess_game_logic.requests.PromotePieceRequest;
import org.example.exceptions.ErrorMessage;
import org.example.exceptions.GameOverException;
import org.example.exceptions.MovePieceException;
import org.example.exceptions.PromInfoNeededException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Lobby {
    @Getter
    private final Long idGame;
    @Getter
    private final Long idPlayer1;
    @Getter
    private final Long idPlayer2;
    @Getter
    private final MoveValidator moveValidator;
    private volatile Long currentPlayerId;
    private volatile boolean needsPromotionInfo=false;
    @Setter
    private volatile  boolean stillPlaying=true;
    private Thread threadTimer;
    Map<Long,Timer> timerMap=new HashMap<Long,Timer>();
    public Lobby(Long idGame, Long idPlayer1, Long idPlayer2, MoveValidator moveValidator,int nrMinutes) {
        this.idGame = idGame;
        this.idPlayer1 = idPlayer1;
        this.idPlayer2 = idPlayer2;
        this.moveValidator = moveValidator;
        this.currentPlayerId = idPlayer1;
        Timer timer1=new Timer(nrMinutes);
        Timer timer2=new Timer(nrMinutes);
        timerMap.put(idPlayer1,timer1);
        timerMap.put(idPlayer2,timer2);
        threadTimer=new Thread(timerMap.get(idPlayer1));
        threadTimer.start();

    }

    public synchronized void processMove(MovePieceRequest request) throws JsonProcessingException {
        System.out.println("Is game still going?:"+stillPlaying);
        if(!stillPlaying)
            throw new GameOverException(ErrorMessage.GameOver.get());
        try {
            if(needsPromotionInfo)
                throw new MovePieceException("Server can't respond to this request now!");
            if (!Objects.equals(currentPlayerId, request.getIdPlayer())) {
                throw new MovePieceException("Wait your turn");
            }

            ChessMoveType moveType = getMoveType(request);
            if (moveType == ChessMoveType.WrongMove) {
                throw new MovePieceException("Incorrect move");
            }
            Color playerColor;
            if (Objects.equals(currentPlayerId, idPlayer1))
                playerColor = Color.White;
            else
                playerColor = Color.Black;
            moveValidator.processMoveRequest(request, moveType, playerColor);
            switchPlayer();
        }
        catch(PromInfoNeededException  e){
            needsPromotionInfo=true;
            throw e;
        }
        catch (GameOverException e){
            stillPlaying=false;
           threadTimer.interrupt();
            throw new GameOverException(e.getMessage());
        }


    }
    public void processPromoteRequest(PromotePieceRequest request){
//        try {
            if (!needsPromotionInfo)
                throw new RuntimeException("You can't promote!");
            if (!Objects.equals(request.getIdPlayer(), currentPlayerId))
                throw new RuntimeException("Wait your turn!");
            moveValidator.promote(request);
            needsPromotionInfo = false;
            switchPlayer();
//        }
//        catch(GameOverException )
    }
    public GameResult forfeit(Long idPlayer){
        threadTimer.interrupt();
        String message="Player resigned!";
        GameResult gameResult=new GameResult("Win",message);
        System.out.println("Lobby fct: "+gameResult);
        stillPlaying=false;
       return gameResult;

    }
    private void switchPlayer() {
        currentPlayerId = currentPlayerId.equals(idPlayer1) ? idPlayer2 : idPlayer1;
        System.out.println("Current player: " + currentPlayerId);
        threadTimer.interrupt();
        threadTimer=new Thread(timerMap.get(currentPlayerId));
        threadTimer.start();
    }

    private boolean validateCoordinates(MovePieceRequest request) {
        return request.getXCurrent() >= 0 && request.getXCurrent() <= 7 &&
                request.getYCurrent() >= 0 && request.getYCurrent() <= 7 &&
                request.getXDestination() >= 0 && request.getXDestination() <= 7 &&
                request.getYDestination() >= 0 && request.getYDestination() <= 7;
    }

    private boolean isKnightMove(int xCurrent, int yCurrent, int xDest, int yDest) {
        int dx = Math.abs(xDest - xCurrent);
        int dy = Math.abs(yDest - yCurrent);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }
    private boolean isPawnMove(int xCurrent,int yCurrent,int xDest,int yDest){
        return Math.abs(xCurrent-xDest)<=2 && yCurrent==yDest;
    }
    private boolean isKingMove(int xCurrent,int yCurrent,int xDest,int yDest){
        return Math.abs(xCurrent-xDest)<=1 && Math.abs(yCurrent-yDest)<=1;
    }
    private ChessMoveType getMoveType(MovePieceRequest request) {

        if (!validateCoordinates(request)) return ChessMoveType.WrongMove;

        int xCurrent=request.getXCurrent(),yCurrent=request.getYCurrent();
        int xDest=request.getXDestination(),yDest=request.getYDestination();
        if (xCurrent==xDest && yCurrent==yDest)
            return ChessMoveType.WrongMove;

        if (xCurrent==xDest) return ChessMoveType.Horizontal;
        if (yCurrent==yDest) return ChessMoveType.Vertical;
        if (Math.abs(xCurrent - xDest) ==
                Math.abs(yCurrent-yDest))
            return ChessMoveType.Diagonal;
        if (isKnightMove(xCurrent,yCurrent,xDest,yDest))
            return ChessMoveType.KnightMove;
        return ChessMoveType.WrongMove;
    }


}
