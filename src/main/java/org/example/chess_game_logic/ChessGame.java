package org.example.chess_game_logic;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

//@Component
//@Scope("prototype")
public class ChessGame {
    private final Long idGame;
    @Getter
    private final Long idPlayer1;
    @Getter
    private final  Long idPlayer2;
    private Long currentPlayerId;
    ChessGame(Long idGame,Long idPlayer1,Long idPlayer2){
        this.idGame=idGame;
        this.idPlayer1=idPlayer1;
        this.idPlayer2=idPlayer2;
        currentPlayerId=idPlayer1;
    }
    synchronized void processMove(MovePieceRequest request){
        synchronized (this) {
            if(!Objects.equals(currentPlayerId,request.getIdPlayer()))
                throw new MovePieceException("Asteapta-ti randul");
            ChessMoveType moveType = this.getMoveType(request);
            if (moveType == ChessMoveType.WrongMove)
                throw new MovePieceException("Mutare incorecta");

        }
    }
    public static boolean isKnightMove(int xCurrent, int yCurrent, int xDest, int yDest) {
        int dx = Math.abs(xDest - xCurrent);
        int dy = Math.abs(yDest - yCurrent);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }
    ChessMoveType getMoveType(MovePieceRequest request){
        if(request.getXCurrent()==request.getXDestination() && request.getYCurrent()== request.getYDestination())
            return ChessMoveType.WrongMove;
        if(request.getXCurrent()==request.getXDestination())
            return  ChessMoveType.Vertical;
        if(request.getYCurrent()==request.getYDestination())
            return ChessMoveType.Horizontal;
        if( Math.abs(request.getXCurrent() -request.getXDestination()) == Math.abs(request.getYCurrent()- request.getYDestination()))
            return  ChessMoveType.Diagonal;
        if(isKnightMove(request.getXCurrent(), request.getYCurrent(), request.getXDestination(), request.getYDestination()))
            return ChessMoveType.KnightMove;

        return ChessMoveType.WrongMove;
    }
}
