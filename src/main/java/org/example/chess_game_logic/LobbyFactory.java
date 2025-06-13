package org.example.chess_game_logic;

import org.example.chess_game_logic.entities.ChessGameRepository;
import org.example.chess_game_logic.entities.ChessMoveRepository;
import org.example.chess_game_logic.entities.GameResultEntityRepository;
import org.example.chess_game_logic.timer.TimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LobbyFactory {

    @Autowired
    private TimerService timerService;

    @Autowired
    private ChessMoveRepository chessMoveRepository;

    @Autowired
    private ChessGameRepository chessGameRepository;
    @Autowired
    private GameResultEntityRepository gameResultEntityRepository;

    public Lobby createLobby(Long idLobby, Long idPlayer1, Long idPlayer2, MoveValidator moveValidator, int minutes) {
        return new Lobby(idLobby, idPlayer1, idPlayer2, moveValidator, minutes, timerService,chessMoveRepository,chessGameRepository,gameResultEntityRepository);
    }

}
