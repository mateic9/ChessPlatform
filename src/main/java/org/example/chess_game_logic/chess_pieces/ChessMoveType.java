package org.example.chess_game_logic.chess_pieces;

import org.example.chess_game_logic.MovePieceRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceArray;

public enum ChessMoveType {
    WrongMove{
        @Override
        public String toString(){
            return "Wrong move";
        }
    },

    Vertical {
        @Override
        public String toString() {
            return "Vertical move";
        }

        @Override
        public int getOffsetX(Position c, Position d) {
            return c.getX() < d.getX() ? 1 : -1;
        }

        @Override
        public int getOffsetY(Position c, Position d) {
            return 0;
        }
    },

    Horizontal {
        @Override
        public String toString() {
            return "Horizontal move";
        }

        @Override
        public int getOffsetX(Position c, Position d) {
            return 0;
        }

        @Override
        public int getOffsetY(Position c, Position d) {
            return c.getY() < d.getY() ? 1 : -1;
        }
    },

    Diagonal {
        @Override
        public String toString() {
            return "Diagonal move";
        }

        @Override
        public int getOffsetX(Position c, Position d) {
            return c.getX() < d.getX() ? 1 : -1;
        }

        @Override
        public int getOffsetY(Position c, Position d) {
            return c.getY() < d.getY() ? 1 : -1;
        }
    },



    KnightMove {

        @Override
        public String toString(){
            return "Knight move";
        }
    };


    // Default method for enums that don't override it

    public String toString(){
        throw new UnsupportedOperationException("Offsets not defined for " + this.name());
    }
    public int getOffsetX(Position curPosition,Position destPosition){
        throw new UnsupportedOperationException("Offsets not defined for " + this.name());
    }
    public int getOffsetY(Position curPosition,Position destPosition){
        throw new UnsupportedOperationException("Offsets not defined for " + this.name());
    }

}
