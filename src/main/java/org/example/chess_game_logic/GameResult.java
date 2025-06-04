package org.example.chess_game_logic;

import java.util.HashMap;
import java.util.Map;

public class GameResult {
    private String matchResult;

    private String reason;
    public GameResult() {}  // No-arg constructor needed

    public GameResult(String matchResult,String reason) {
        this.matchResult = matchResult;

        this.reason=reason;
    }
    @Override
    public String toString(){
        return "Result:"+matchResult+"/"+"Reason:"+reason;
    }
    public Map<String,Object> toJson(){
        Map<String,Object> body=new HashMap<String,Object>();
        body.put("matchResult",matchResult);

        body.put("reason",reason);
        return body;
    }
}
