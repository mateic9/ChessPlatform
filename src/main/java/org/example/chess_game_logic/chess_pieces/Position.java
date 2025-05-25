package org.example.chess_game_logic.chess_pieces;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    @Override
    public boolean equals(Object obj){
        if(obj==null || ! (obj instanceof Position o))
            return false;
        return o.getX()==this.getX() && o.getY()==this.getY();
    }


}