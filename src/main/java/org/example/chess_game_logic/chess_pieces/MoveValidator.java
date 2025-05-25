package org.example.chess_game_logic.chess_pieces;

import org.example.chess_game_logic.MovePieceException;
import org.example.chess_game_logic.MovePieceRequest;
import org.example.entities.PromInfoNeededException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MoveValidator {

    private final Board board;

  MoveValidator(Board board){
    this.board=board;
  }
  public void processMoveRequest(MovePieceRequest request, ChessMoveType moveType,Color playerColor){
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
          throw new MovePieceException("King check not done");
      if(!this.isKingSafe(curPosition,destPosition,board,playerColor))
          throw new MovePieceException("King "+playerColor+" is in check!" );
      board.movePiece(curPosition,destPosition);
      System.out.println();
      System.out.println("Board config:");
      board.printBoard();

      if(piece instanceof Pawn){
          if(((Pawn)piece).canPromote(destPosition))
              throw new PromInfoNeededException("Alege cu ce piesa vrei sa promovezi!");
      }
    }
    private boolean isKingSafe(Position curPosition, Position destPosition, Board board, Color playerColor) {
        board.movePiece(curPosition, destPosition);
        try {
            PieceInterface king = board.getPieceAt(board.getKingPozMap().get(playerColor));
            System.out.println("Selected piece as king :"+king);
            return !(king instanceof King) || !((King) king).isInCheck(board);
        } finally {
            board.movePiece(destPosition, curPosition);  // undo move
        }
    }

    private boolean isCastleSafe(Position kingPoz,Position rookPoz,Color playerColor){
        board.makeCastle(kingPoz, rookPoz);
        try {
            PieceInterface king = board.getPieceAt(board.getKingPozMap().get(playerColor));
            System.out.println("Selected piece as king :"+king);
            return !(king instanceof King) || !((King) king).isInCheck(board);
        } finally {
            board.undoCastle(kingPoz,rookPoz);  // undo move
        }

    }






}
