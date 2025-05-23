package org.example.chess_game_logic;
import  org.example.chess_game_logic.chess_pieces.MoveValidator;

import lombok.Getter;
import org.example.chess_game_logic.chess_pieces.ChessMoveType;

import java.util.Objects;

//@Component
//@Scope("prototype")
public class ChessGame {
    @Getter
    private final Long idGame;
    @Getter
    private final Long idPlayer1;
    @Getter
    private final  Long idPlayer2;
    private final MoveValidator moveValidator;
    private volatile Long currentPlayerId;
    ChessGame(Long idGame,Long idPlayer1,Long idPlayer2){
        this.idGame=idGame;
        this.idPlayer1=idPlayer1;
        this.idPlayer2=idPlayer2;
        currentPlayerId=idPlayer1;
        this.moveValidator=new MoveValidator();
    }
    synchronized void processMove(MovePieceRequest request){
        synchronized (this) {
            if(!Objects.equals(currentPlayerId,request.getIdPlayer()))
                throw new MovePieceException("Asteapta-ti randul");
            ChessMoveType moveType = this.getMoveType(request);
            if (moveType == ChessMoveType.WrongMove)
                throw new MovePieceException("Mutare incorecta");
           moveValidator.processMoveRequest(request,moveType);
           switchPlayer();
        }
    }
    void switchPlayer(){
        System.out.println("Id player 1:"+idPlayer1);
        System.out.println("Id player 2:"+idPlayer2);
        if(currentPlayerId.equals(idPlayer1))
            currentPlayerId=idPlayer2;
        else
            currentPlayerId=idPlayer1;
        System.out.println("Current id player:"+currentPlayerId);
    }
    public boolean validateCoordinates(MovePieceRequest request){
      if(request.getXCurrent()<0 || request.getXCurrent()>7)
          return false;
      if(request.getYCurrent()<0 || request.getYCurrent()>7)
          return false;
      if(request.getXDestination()<0 || request.getXDestination()>7)
            return false;
      if(request.getYDestination()<0 || request.getYDestination()>7)
          return false;
      return true;
    }
    public static boolean isKnightMove(int xCurrent, int yCurrent, int xDest, int yDest) {
        int dx = Math.abs(xDest - xCurrent);
        int dy = Math.abs(yDest - yCurrent);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    ChessMoveType getMoveType(MovePieceRequest request){

        if(!validateCoordinates(request))
            return ChessMoveType.WrongMove;
//        System.out.println(0);
        if(request.getXCurrent()==request.getXDestination() && request.getYCurrent()== request.getYDestination())
            return ChessMoveType.WrongMove;
//        System.out.println(1);
        if(request.getXCurrent()==request.getXDestination())
            return  ChessMoveType.Vertical;
//        System.out.println(2);
        if(request.getYCurrent()==request.getYDestination())
            return ChessMoveType.Horizontal;
//        System.out.println(3);
        if( Math.abs(request.getXCurrent() -request.getXDestination()) == Math.abs(request.getYCurrent()- request.getYDestination()))
            return  ChessMoveType.Diagonal;
//        System.out.println(4);
        if(isKnightMove(request.getXCurrent(), request.getYCurrent(), request.getXDestination(), request.getYDestination()))
            return ChessMoveType.KnightMove;
//        System.out.println(5);
        return ChessMoveType.WrongMove;
    }
}

