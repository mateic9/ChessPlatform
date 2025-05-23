package org.example.chess_game_logic.chess_pieces;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
public class Queen implements  ChessPieceInterface{
    private final int value;
    private final Color color;
    private final List<ChessMoveType> moveTypes;
    Queen(int value, Color color,List<ChessMoveType> moveTypes){
        this.value=value;
        this.color=color;
        this.moveTypes=moveTypes;
    }

}
