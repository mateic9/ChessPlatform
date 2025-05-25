package org.example.chess_game_logic.chess_pieces;
import java.util.List;
public interface PieceInterface {

    public int getValue();
    public List<ChessMoveType> getMoveTypes();
    public Color getColor();
    public String toString();
    public boolean canMove(Position curPosition,Position destPosition, ChessMoveType moveType, Board board);
    public boolean canCapture(Position curPosition,Position destPosition, ChessMoveType moveType, Board board);
    public String getSymbol();
}
