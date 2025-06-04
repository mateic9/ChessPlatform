package org.example.chess_game_logic;

import java.util.HashMap;
import java.util.Map;

public class GameResult {
    private String matchResult;
    private  Long winnerId;
    private String reason;
    public GameResult() {}  // No-arg constructor needed

    public GameResult(String matchResult, Long winnerId,String reason) {
        this.matchResult = matchResult;
        this.winnerId = winnerId;
        this.reason=reason;
    }
    @Override
    public String toString(){
        return "Result:"+matchResult+"/"+"winnerId:"+winnerId+"Reason:"+reason;
    }
    public Map<String,Object> toJson(){
        Map<String,Object> body=new HashMap<String,Object>();
        body.put("matchResult",matchResult);
        body.put("winnerId",winnerId);
        body.put("reason",reason);
        return body;
    }
}
