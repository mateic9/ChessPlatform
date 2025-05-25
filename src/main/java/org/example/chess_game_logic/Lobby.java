package org.example.chess_game_logic;

import lombok.Getter;
import org.example.chess_game_logic.chess_pieces.MoveValidator;
import org.example.chess_game_logic.chess_pieces.ChessMoveType;
import org.example.chess_game_logic.chess_pieces.Color;
import org.example.entities.PromInfoNeededException;

import java.util.Objects;

public class Lobby {
    @Getter
    private final Long idGame;
    @Getter
    private final Long idPlayer1;
    @Getter
    private final Long idPlayer2;
    private final MoveValidator moveValidator;
    private volatile Long currentPlayerId;
    private volatile boolean needsPromotionInfo=false;

    public Lobby(Long idGame, Long idPlayer1, Long idPlayer2, MoveValidator moveValidator) {
        this.idGame = idGame;
        this.idPlayer1 = idPlayer1;
        this.idPlayer2 = idPlayer2;
        this.moveValidator = moveValidator;
        this.currentPlayerId = idPlayer1;
    }

    public synchronized void processMove(MovePieceRequest request) {
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


    }
    public void processPromoteRequest(PromotePieceRequest request){
        if(!needsPromotionInfo)
            throw new RuntimeException("You can't promote!");
        if(request.getIdPlayer()!=currentPlayerId)
            throw new RuntimeException("Wait your turn!");
        moveValidator.promote(request);
    }

    private void switchPlayer() {
        currentPlayerId = currentPlayerId.equals(idPlayer1) ? idPlayer2 : idPlayer1;
        System.out.println("Current player: " + currentPlayerId);
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
