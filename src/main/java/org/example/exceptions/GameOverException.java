package org.example.exceptions;

import lombok.Setter;
import org.example.chess_game_logic.GameResult;

public class GameOverException extends RuntimeException{
    @Setter
    private  GameResult gameResult;
    public GameOverException(String message){
        super(message);
    }
    public GameOverException(String message, GameResult result){
        this.gameResult=result;
    }
    public String toString(){
        return "GameOver Exc: "+this.getMessage();
    }
}

