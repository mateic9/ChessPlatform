package org.example.authentification;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
    private String ip;
    private int port;
}

