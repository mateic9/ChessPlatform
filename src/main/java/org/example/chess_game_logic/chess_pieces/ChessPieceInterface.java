package org.example.chess_game_logic.chess_pieces;
import java.util.List;
public interface ChessPieceInterface {

    public double getValue();
    public List<ChessMoveType> getMoveTypes();
    public Color getColor();
}
