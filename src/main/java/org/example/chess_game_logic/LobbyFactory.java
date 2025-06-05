package org.example.chess_game_logic;

import org.example.chess_game_logic.timer.TimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LobbyFactory {

    @Autowired
    private TimerService timerService;

    public Lobby createLobby(Long idGame, Long idPlayer1, Long idPlayer2, MoveValidator moveValidator, int minutes) {
        return new Lobby(idGame, idPlayer1, idPlayer2, moveValidator, minutes, timerService);
    }
}
