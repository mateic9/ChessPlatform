package org.example.chess_game_logic;

import lombok.Getter;

@Getter
public class MovePieceRequest {
    private final Long idPlayer;
    private final int xCurrent, yCurrent,xDestination,yDestination;
    MovePieceRequest(Long idPlayer,int x1,int y1,int x2,int y2){
        this.idPlayer=idPlayer;
        this.xCurrent=x1;
        this.yCurrent =y1;
        this.xDestination=x2;
        this.yDestination=y2;
    }

}
