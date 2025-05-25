package org.example.chess_game_logic.chess_pieces;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class Knight implements PieceInterface {
    private final int value = 3;
    private final Color color;
    private final List<ChessMoveType> moveTypes = Arrays.asList(
            ChessMoveType.KnightMove

    );

    Knight(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Knight " + color;
    }

    public boolean canMove(Position curPosition, Position destPosition, ChessMoveType moveType, Board board) {
        System.out.println("Trying to perform move "+moveType+" on piece "+this);
        if (!moveTypes.contains(moveType)) return false;



        return true;
    }


    public boolean canCapture(Position curPosition, Position destPosition, ChessMoveType moveType, Board board){
        if(!moveTypes.contains(moveType))
            return false;
        return true;
    }
    public String getSymbol(){
        if(color==Color.White)
            return "n";
        else
            return "N";
    }

}
