package org.example.chess_game_logic.chess_pieces;

import lombok.Getter;
import org.example.chess_game_logic.Board;

import java.util.Arrays;
import java.util.List;
@Getter
public class Bishop implements PieceInterface{

    private final int value = 3;
    private final Color color;
    private final List<ChessMoveType> moveTypes = Arrays.asList(
            ChessMoveType.Diagonal

    );

    public Bishop(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Bishop " + color;
    }

    public boolean canMove(Position curPosition, Position destPosition, ChessMoveType moveType, Board board) {
        System.out.println("Trying to perform move "+moveType+" on piece "+this);
        if (!moveTypes.contains(moveType)) return false;

       if (moveType == ChessMoveType.Diagonal)
            return canMoveLinear(curPosition, destPosition, moveType, board);

        return false;
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
            if (!board.isOnBoard(intermediatePos)) return true;
            if (board.getPieceAt(intermediatePos) != null) break;
            curX += offsetX;
            curY += offsetY;
        }

        // Final destination piece check
        PieceInterface pieceAtDest = board.getPieceAt(destPosition);
        System.out.println("Encountered on move "+moveType+" "+pieceAtDest);
        if (pieceAtDest != null && (pieceAtDest.getColor() == color ||(pieceAtDest instanceof Bishop)))
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
            if (!board.isOnBoard(intermediatePos)) return true;
            if (board.getPieceAt(intermediatePos) != null) break;
            curX += offsetX;
            curY += offsetY;
        }

        // Final destination piece check
        PieceInterface pieceAtDest = board.getPieceAt(destPosition);
        System.out.println("Encountered on move "+moveType+" "+pieceAtDest);
        if (pieceAtDest != null && (pieceAtDest.getColor() == color ))
            return false;

        return true;
    }
    public String getSymbol(){
        if(color==Color.White)
            return "B";
        else
            return "b";
    }
}
