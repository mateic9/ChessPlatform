package org.example.chess_game_logic.chess_pieces;

import lombok.Getter;
import lombok.Setter;
import org.example.chess_game_logic.Board;

import java.util.Arrays;
import java.util.List;
@Getter
public class Rook implements PieceInterface {
    private final int value = 5;
    private final Color color;

    @Setter
    private boolean moved;
    private final List<ChessMoveType> moveTypes = Arrays.asList(
            ChessMoveType.Diagonal,
            ChessMoveType.Horizontal,
            ChessMoveType.Vertical
    );

    public Rook(Color color) {
        this.color = color;
        this.moved=false;
    }

    @Override
    public String toString() {
        return "Rook " + color;
    }

    public boolean canMove(Position curPosition, Position destPosition, ChessMoveType moveType, Board board) {
        if (!moveTypes.contains(moveType)) return false;
       return canMoveLinear(curPosition,destPosition ,moveType, board);

    }

    private boolean canMoveLinear(Position curPosition, Position destPosition, ChessMoveType moveType, Board board) {
        int offsetX = moveType.getOffsetX(curPosition, destPosition);
        int offsetY = moveType.getOffsetY(curPosition, destPosition);

        int curX = curPosition.getX();
        int curY = curPosition.getY();
        int destX = destPosition.getX();
        int destY = destPosition.getY();

        curX += offsetX;
        curY += offsetY;

        while (curX != destX || curY != destY) {
            Position intermediatePos = new Position(curX, curY);
            if (!board.isOnBoard(intermediatePos)) return false;
            if (board.getPieceAt(intermediatePos) != null) return false;
            curX += offsetX;
            curY += offsetY;
        }

        // Final destination piece check
        PieceInterface pieceAtDest = board.getPieceAt(destPosition);
        if (pieceAtDest != null && (pieceAtDest.getColor() == color ||(pieceAtDest instanceof King)))
            return false;

        return true;
    }
    public boolean canCapture(Position curPosition, Position destPosition, ChessMoveType moveType, Board board){
        if(!moveTypes.contains(moveType))
            return false;
        int offsetX = moveType.getOffsetX(curPosition, destPosition);
        int offsetY = moveType.getOffsetY(curPosition, destPosition);

        int curX = curPosition.getX();
        int curY = curPosition.getY();
        int destX = destPosition.getX();
        int destY = destPosition.getY();

        curX += offsetX;
        curY += offsetY;

        while (curX != destX || curY != destY) {
            Position intermediatePos = new Position(curX, curY);
            if (!board.isOnBoard(intermediatePos)) return false;
            if (board.getPieceAt(intermediatePos) != null) return false;
            curX += offsetX;
            curY += offsetY;
        }

        // Final destination piece check
        PieceInterface pieceAtDest = board.getPieceAt(destPosition);
        if (pieceAtDest != null && (pieceAtDest.getColor() == color ))
            return false;

        return true;
    }
    public String getSymbol(){
        if(color==Color.White)
            return "r";
        else
            return "R";
    }
}
