package org.example.practice;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PracticeGame {
    private final int userId;
    private final String difficulty;
    private final String playerColor;
    private final List<MoveRecord> history = new ArrayList<>();
    private String fen;
    private int curFenIdx;
    private boolean playerTurn;

    public PracticeGame(int  userId, String fen, String color, String difficulty) {
        this.userId = userId;
        this.fen = fen;
        this.playerColor = color;
        this.difficulty = difficulty;
        this.playerTurn = "white".equalsIgnoreCase(color);
        curFenIdx=0;
    }

    public void applyMove(String from, String to, String updatedFen) {
        history.add(new MoveRecord(from, to, this.fen));
        this.fen = updatedFen;
        this.playerTurn = !playerTurn;
        curFenIdx++;
    }



}

