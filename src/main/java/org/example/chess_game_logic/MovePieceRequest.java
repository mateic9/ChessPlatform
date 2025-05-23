package org.example.chess_game_logic;

import lombok.Getter;

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


}