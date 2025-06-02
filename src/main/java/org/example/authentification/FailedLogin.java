package org.example.authentification;

public class FailedLogin extends RuntimeException{
    FailedLogin(String message){
        super(message);
    }
}
