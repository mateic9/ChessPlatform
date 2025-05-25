package org.example.chess_game_logic.chess_pieces;



import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
public class Pawn implements PieceInterface {
    private final int value = 1;

    @Setter
    private boolean moved;

    private final Color color;

    // Pawn uses vertical for movement, diagonal for capturing
    private final List<ChessMoveType> moveTypes = Arrays.asList(
            ChessMoveType.Vertical,
            ChessMoveType.Diagonal
    );

    public Pawn(Color color) {
        this.color = color;
        this.moved = false;
    }

    @Override
    public String toString() {
        return "Pawn " + color;
    }

    @Override
    public boolean canMove(Position curPosition, Position destPosition, ChessMoveType moveType, Board board) {
        if(!moveTypes.contains(moveType))
            return false;
        if(moveType==ChessMoveType.Vertical)
            return canMoveVertical( curPosition, destPosition,board);
       else
            return canMoveDiagonal(curPosition, destPosition,board);


    }
    public boolean canMoveVertical(Position curPos,Position destPos,Board board){
        int offsetX=ChessMoveType.Vertical.getOffsetX(curPos,destPos);
        int difX=Math.abs(curPos.getX()-destPos.getX());
        if(difX>2|| difX==0)
            return false;

        int i;
        for(i=1;i<=difX;i++){
            Position p=new Position(curPos.getX()+i*offsetX, curPos.getY());
            if(!board.isOnBoard(p)||board.getPieceAt(p)!=null)
                return  false;

        }

        return difX != 2 || !this.isMoved();
    }
    public boolean canMoveDiagonal(Position curPos,Position destPos,Board board){
        int offsetX=Math.abs(curPos.getX()-destPos.getX());
        int offsetY=Math.abs(curPos.getY()-destPos.getY());
        if(offsetY!=1||offsetX!=1)
            return false;
        return board.getPieceAt(destPos)!=null;
    }

    @Override
    public boolean canCapture(Position curPosition, Position destPosition, ChessMoveType moveType, Board board) {
        if(!moveTypes.contains(moveType)||moveType==ChessMoveType.Vertical)
            return false;

        return canMoveDiagonal(curPosition,destPosition,board);
    }

    public String getSymbol() {
        return (color == Color.White) ? "p" : "P";
    }
    public boolean canPromote(Position destPosition){
        if(color==Color.White&&destPosition.getX()==7)
            return true;
        return color == Color.Black && destPosition.getX() == 0;
    }
}
