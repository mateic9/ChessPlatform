package org.example.chess_game_logic.chess_pieces;

import org.example.chess_game_logic.MovePieceException;
import org.example.chess_game_logic.MovePieceRequest;
import org.example.chess_game_logic.MovePieceException;
import java.util.Arrays;
import java.util.List;

public class MoveValidator {
    private ChessPieceInterface[] board = new ChessPieceInterface[64];
    public MoveValidator(){
     this.initializeBoard();
    }
   private  void initializeBoard(){
        this.board[3]=new Queen(9,Color.White, Arrays.asList(ChessMoveType.Diagonal,ChessMoveType.Horizontal,ChessMoveType.Vertical));


   }
    public void processMoveRequest(MovePieceRequest request,ChessMoveType moveType){
       int pieceIndex=this.getIndex(request.getXCurrent() ,request.getYCurrent());
       ChessPieceInterface selectedPiece=board[pieceIndex];
       if(selectedPiece==null)
           throw new MovePieceException("No piece selected");
       if(!selectedPiece.getMoveTypes().contains(moveType))
           throw new MovePieceException("Incorrect Move");
       ///King check
      ///Pawn check
      ///knight check (easier because i don't have to check if there are any pieces in the way top the destination)

      if(this.isMoveValid(request,moveType,selectedPiece.getColor())){

      }
   }
   boolean isMoveValid(MovePieceRequest request,ChessMoveType moveType,Color color){
        int currentIdx=this.getIndex(request.getXCurrent(), request.getYCurrent());
        int destintaionIdx=this.getIndex(request.getXDestination(), request.getYDestination());
       if(board[destintaionIdx]!=null && board[destintaionIdx].getColor()==color)
           throw new MovePieceException("You already have a piece on ("+ request.getXDestination()+" , "+request.getYDestination()+")" );
       int offset=moveType.getOffset(request);

      currentIdx+=offset;
      while(currentIdx!=destintaionIdx && board[currentIdx]==null){
          currentIdx+=offset;
      }
      if(currentIdx!=destintaionIdx)
          throw new MovePieceException("Pieces in the way square "+destintaionIdx);
      return true;
   }

    int getIndex(int row, int col) {
        return row * 8 + col;
    }




}
