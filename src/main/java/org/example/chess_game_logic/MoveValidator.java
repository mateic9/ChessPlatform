package org.example.chess_game_logic;

import lombok.Getter;
import org.example.chess_game_logic.chess_pieces.*;
import org.example.chess_game_logic.entities.ChessMove;
import org.example.chess_game_logic.requests.MovePieceRequest;
import org.example.chess_game_logic.requests.PromotePieceRequest;
import org.example.exceptions.ErrorMessage;
import org.example.exceptions.GameOverException;
import org.example.exceptions.MovePieceException;
import org.example.exceptions.PromInfoNeededException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class MoveValidator {
   @Getter
    private final Board board;
    private Position promotePos;
    private  Map<Position,ChessMoveType> attacksOnKing=new HashMap<Position,ChessMoveType>();
  MoveValidator(Board board){
    this.board=board;
  }
  public void processMoveRequest(MovePieceRequest request, ChessMoveType moveType, Color playerColor){
      Position curPosition=request.mapCurPosition();
      Position destPosition=request.mapDestPosition();
      PieceInterface piece=board.getPieceAt(curPosition);

      if(piece==null)
        throw new MovePieceException("No piece selected!");
      System.out.println(piece+" "+ playerColor+ " selected");
      if(piece.getColor()!=playerColor)
          throw new MovePieceException("This piece isn't yours!");
      if(!(piece instanceof King) ) {
          if (!piece.canMove(curPosition, destPosition, moveType, board)) {
              throw new MovePieceException("Move can not be executed!");
          }
      }
      else{

          if(!piece.canMove(curPosition, destPosition, moveType, board)){
              Position rookCastlePoz=((King)piece).getPositionCastle(curPosition, destPosition, moveType, board);
              if(rookCastlePoz==null)
                  throw new MovePieceException("Castle can't be executed!");
              if(!isCastleSafe(curPosition,rookCastlePoz,playerColor))
                  throw new MovePieceException("Castle not done!King in check!!");
              board.makeCastle(curPosition,rookCastlePoz);
              System.out.println("Board config:");
              board.printBoard();
              return;
          }

      }

      Position p=board.getKingPozMap().get(playerColor);
      PieceInterface selectedKing= board.getPieceAt(p);

      if(!(selectedKing instanceof King))
          throw new MovePieceException("Position of king was lost");
      if(!this.isKingSafe(curPosition,destPosition,board,playerColor))
          throw new MovePieceException("King "+playerColor+" is in check!" );
      board.movePiece(curPosition,destPosition);
      boolean isCapture = board.getPieceAt(destPosition) != null;
      board.registerActualMove(curPosition, destPosition, playerColor, isCapture);

//      System.out.println();
//      System.out.println("Board config:");
//      board.printBoard();

      if(piece instanceof Pawn){
          if(((Pawn)piece).canPromote(destPosition)) {
              System.out.println("Waiting for promotion choice!");
              System.out.println("Poz destinatie: " + destPosition);
              promotePos = destPosition;
              throw new PromInfoNeededException("Alege cu ce piesa vrei sa promovezi!");
          }
      }
      if(this.isGameOver(playerColor,board)) {
          System.out.println("GAME OVER!");
          System.out.println("Board config:");
          board.printBoard();
          throw new GameOverException(ErrorMessage.CheckMate.get());
      }
//      System.out.println("The game isn't over");
//      System.out.println("Board config:");
      System.out.println(board.getRealFen(playerColor));
      board.printBoard();
    }
//    private boolean isKingSafe(Position curPosition, Position destPosition, Board board, Color playerColor) {
//        board.movePiece(curPosition, destPosition);
//        try {
//            PieceInterface king = board.getPieceAt(board.getKingPozMap().get(playerColor));
//            System.out.println("Selected piece as king :"+king);
//            if(!(king instanceof King))
//                return false;
//            attacksOnKing=((King)king).getCheckDirections(board);
//            return attacksOnKing.isEmpty();
//        } finally {
//            board.movePiece(destPosition, curPosition);  // undo move
//        }
//    }

    private boolean isKingSafe(Position curPosition, Position destPosition, Board board, Color playerColor) {
        PieceInterface piece = board.getPieceAt(curPosition);
        boolean isKing = piece instanceof King;
        Position originalKingPos = board.getKingPozMap().get(playerColor);
        PieceInterface capturedPiece= board.getPieceAt(destPosition);
        boolean initialMovedValue=true;
      if(capturedPiece!=null){
          if(capturedPiece instanceof Pawn)
              initialMovedValue=((Pawn)capturedPiece).isMoved();
          if(capturedPiece instanceof Rook)
              initialMovedValue=((Rook)capturedPiece).isMoved();
          if(capturedPiece instanceof King)
              initialMovedValue=((King)capturedPiece).isMoved();
      }
        board.movePiece(curPosition, destPosition);
        if (isKing) {
            board.getKingPozMap().put(playerColor, destPosition); // update kingPozMap
        }

        try {
            PieceInterface king = board.getPieceAt(board.getKingPozMap().get(playerColor));
            if (!(king instanceof King))
                return false;
            attacksOnKing = ((King) king).getCheckDirections(board);
            return attacksOnKing.isEmpty();
        } finally {
            board.movePiece(destPosition, curPosition); // undo the move
            if (isKing) {
                board.getKingPozMap().put(playerColor, originalKingPos); // restore kingPozMap
            }
            if(capturedPiece!=null) {
                board.setPieceAt(destPosition, capturedPiece);
                if(capturedPiece instanceof Pawn)
                    ((Pawn)capturedPiece).setMoved(initialMovedValue);
                if(capturedPiece instanceof Rook)
                    ((Rook)capturedPiece).setMoved(initialMovedValue);
                if(capturedPiece instanceof King)
                    ((King)capturedPiece).setMoved(initialMovedValue);
            }
        }
    }


    private boolean isCastleSafe(Position kingPoz,Position rookPoz,Color playerColor){
        board.makeCastle(kingPoz, rookPoz);
        try {
            PieceInterface king = board.getPieceAt(board.getKingPozMap().get(playerColor));
            System.out.println("Selected piece as king :"+king);
            if(!(king instanceof King))
                return false;
            attacksOnKing=((King)king).getCheckDirections(board);
            return attacksOnKing.isEmpty();
        } finally {
            board.undoCastle(kingPoz,rookPoz);  // undo move
        }



    }


    public void promote(PromotePieceRequest request) {
      String playerChoice=request.getPromotedPiece();
        List<String> possibleChoices= Arrays.asList("Bishop","Knight","Rook","Queen");
        if(!possibleChoices.contains(playerChoice) || promotePos==null)
            throw new MovePieceException("Impossible choice for promotion piece!");
        board.promote(promotePos,playerChoice);
    }

    public boolean isGameOver(Color curPlayerColor,Board board) {

        Color oppPlayerColor=Color.White;
        switch (curPlayerColor) {
            case White:
                oppPlayerColor = Color.Black;
                break;
            case Black:
                oppPlayerColor = Color.White;
                break;
        }
        Position  oppKingPoz=board.getKingPozMap().get(oppPlayerColor);
       List<Position> availableKingPos=board.getFutureKingPos(oppPlayerColor);
//       System.out.println("Available positions: "+availableKingPos.size());
        King oppKing=(King)board.getPieceAt(oppKingPoz);
         if(oppKing.getCheckDirections(board).isEmpty())
             return false;
         System.out.println("King in check!");
       for(Position pozToRun: availableKingPos) {


           if (this.isKingSafe(oppKingPoz, pozToRun, board, oppPlayerColor)) {
               System.out.println("King escapes here:" + pozToRun);
               return false;
           }

       }

//       System.out.println("No position for king "+oppPlayerColor+ " to run!");
//       System.out.println("Pozitie rege advers: "+oppKingPoz);
//       System.out.println("Verificam daca regele mai este: "+board.getPieceAt(oppKingPoz));
       board.printBoard();
       attacksOnKing=((King)board.getPieceAt(oppKingPoz)).getCheckDirections(board);
       if(attacksOnKing.size()>=2)
           return true;
//        System.out.println("Searching for a piece to capture");
       Map<Position,PieceInterface>piecesLeft=board.getPiecesLeftByColor(oppPlayerColor);
//        System.out.println("getting pieces left :"+piecesLeft.size());
        System.out.println(attacksOnKing);
       Position posAttackingPiece=attacksOnKing.keySet().iterator().next();

       for(Position pos: piecesLeft.keySet()){
           PieceInterface piece=piecesLeft.get(pos);

            ChessMoveType move=this.getMoveType(new MovePieceRequest(1L,pos.getX(),pos.getY(),posAttackingPiece.getX(),posAttackingPiece.getY()));
            System.out.println("Detected "+move+" from "+pos+" to "+posAttackingPiece);
            if(move==ChessMoveType.WrongMove)
                continue;
           if(piece.canCapture(pos,posAttackingPiece,move,board)) {
               System.out.println("Check prevented by piece "+piece +" capturing on"+ posAttackingPiece);
               return false;
           }
       }
        System.out.println("No piece can capture attacker!");
       if(attacksOnKing.get(posAttackingPiece)==ChessMoveType.KnightMove)
           return true;
       System.out.println("Phase 3!");
       List<Position> path=attacksOnKing.get(posAttackingPiece).getPath(oppKingPoz,posAttackingPiece);
       for(Position p:path)
           System.out.println(p);
       if(path.isEmpty())
           return true;

        for(Position posPiece: piecesLeft.keySet()){
            PieceInterface piece=piecesLeft.get(posPiece);
            for(Position pathPos:path ){
                ChessMoveType move=this.getMoveType(new MovePieceRequest(1L,posPiece.getX(),posPiece.getY(),pathPos.getX(),pathPos.getY()));
                if(move==ChessMoveType.WrongMove)
                    continue;
                if(piece.canMove(posPiece,pathPos,move,board)) {
                    System.out.println("from "+posPiece+" to "+pathPos);
                    return false;
                }
            }



        }
      return true;
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
