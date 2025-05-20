package org.example.chess_game_logic;

public class MovePieceException extends RuntimeException{
   MovePieceException(String message){
        super(message);
    }
}