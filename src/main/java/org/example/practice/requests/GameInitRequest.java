package org.example.practice.requests;

import lombok.Getter;

@Getter
public class GameInitRequest {
    private String fen;
    private String difficulty;
    private String color;

}



