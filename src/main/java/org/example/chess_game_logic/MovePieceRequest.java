package org.example.chess_game_logic;

import lombok.Getter;
import org.example.chess_game_logic.chess_pieces.Position;
import org.springframework.stereotype.Component;

/**
 * Request object for moving a chess piece
 */
@Getter

public class MovePieceRequest {
    private Long idPlayer;
    private final int xCurrent;
    private  final int yCurrent;
    private final int xDestination;
    private final int yDestination;


    public MovePieceRequest(Long idPlayer, int xCurrent, int yCurrent, int xDestination, int yDestination) {
        this.idPlayer = idPlayer;
        this.xCurrent = xCurrent;
        this.yCurrent = yCurrent;
        this.xDestination = xDestination;
        this.yDestination = yDestination;
    }

    public Position mapCurPosition(){
        return new Position(xCurrent,yCurrent);
    }

    public Position mapDestPosition(){
        return new Position(xDestination,yDestination);
    }


}