package org.example.exceptions;

public class RunOutOfTimeException extends RuntimeException{
    public RunOutOfTimeException(String message){
        super(message);
    }
}

