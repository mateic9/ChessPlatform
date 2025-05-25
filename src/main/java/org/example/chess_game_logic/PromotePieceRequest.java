package org.example.chess_game_logic;

import lombok.Getter;

@Getter
public class PromotePieceRequest {
    private Long idPlayer;
    private final String promotedPiece;
    PromotePieceRequest(Long id,String piece){
        this.idPlayer=id;
        this.promotedPiece=piece;
    }
}
