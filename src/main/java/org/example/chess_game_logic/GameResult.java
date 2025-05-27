package org.example.chess_game_logic;

public class GameResult {
    public String matchResult;
    public Long winnerId;

    public GameResult() {}  // No-arg constructor needed

    public GameResult(String matchResult, Long winnerId) {
        this.matchResult = matchResult;
        this.winnerId = winnerId;
    }
    @Override
    public String toString(){
        return "Result:"+matchResult+"/"+"winnerId:"+winnerId;
    }
}
