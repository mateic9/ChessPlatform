package org.example.chess_game_logic;

public class MovePieceException extends RuntimeException{
   public MovePieceException(String message){
        super(message);
    }
}