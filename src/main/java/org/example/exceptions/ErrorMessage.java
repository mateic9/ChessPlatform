package org.example.exceptions;

public enum ErrorMessage {
    RunOutOfTime("Run out of time"),
    CheckMate("Check mate!"),
    GameOver("The game is over"),
    Draw("It is a draw!");
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String get() {
        return message;
    }

}
