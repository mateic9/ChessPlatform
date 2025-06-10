package org.example.practice;

import lombok.Getter;

@Getter
public class MoveRecord {
    private final String from;
    private final String to;
    private final String fenBeforeMove;
    private final String fenAfterMove;

    public MoveRecord(String from, String to, String fenBeforeMove, String fenAfterMove) {
        this.from = from;
        this.to = to;
        this.fenBeforeMove = fenBeforeMove;
        this.fenAfterMove = fenAfterMove;
    }

    // Getters...
}
