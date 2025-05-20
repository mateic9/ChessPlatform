package org.example.chess_game_logic;


import lombok.Getter;
import lombok.Setter;

@Getter

@Setter
public class JoinGameRequest {

    private Long idPlayer;

    public void setIdPlayer(Long idPlayer) {
        this.idPlayer = idPlayer;
    }
}
