package org.example.exceptions;

public class GameOverException extends RuntimeException{
    public GameOverException(String message){
        super(message);
    }
}

