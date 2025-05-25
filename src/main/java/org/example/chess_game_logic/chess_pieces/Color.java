package org.example.chess_game_logic.chess_pieces;

public enum Color {
    Black{
        @Override
        public String toString() {
            return "BLACK";
        }
    },White{
        @Override
        public String toString() {
            return "WHITE";
        }
    }
}
