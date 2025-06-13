package org.example.exceptions;

public enum ErrorMessage {
    RunOutOfTimeCurrentPlayer("You ran out of time"),
    RunOutOfTimeOpponentPlayer("Opponent ran out of time"),

    CheckMate("Check mate!"),
    GameOver("The game is over"),
    Draw("It is a draw!"),
    Forfeit("the player resigned");
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String get() {
        return message;
    }

}
