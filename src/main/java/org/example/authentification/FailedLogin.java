package org.example.authentification;

public class FailedLogin extends RuntimeException{
    FailedLogin(){
        super("Failed Login!Womp womp!");
    }
}
