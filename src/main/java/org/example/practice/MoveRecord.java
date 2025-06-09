package org.example.practice;

import lombok.Getter;

@Getter
public class MoveRecord {
    private String from;
    private String to;
    private String fenBeforeMove;
    public MoveRecord(String from, String to, String fen) {
        this.from = from;
        this.to = to;
        this.fenBeforeMove = fen;
    }
}
