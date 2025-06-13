package org.example.chess_game_logic;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.chess_game_logic.requests.JoinGameRequest;
import org.example.chess_game_logic.requests.MovePieceRequest;
import org.example.exceptions.MovePieceException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class GamesManagerService {

    private final MoveValidator moveValidator;
    private final LobbyFactory lobbyFactory;

    private final Map<Long, Lobby> activeLobbies = new ConcurrentHashMap<>();
    private final ReentrantLock smallLock = new ReentrantLock();
    private final CyclicBarrier barrier = new CyclicBarrier(2, () -> {
        System.out.println("Barrier reached!");
    });

    private volatile boolean isGameCreated;
    private volatile Long idFirstPlayer = -1L;
    private volatile Long idSecondPlayer = -1L;
    private volatile Lobby lobbyToBeCreated = null;
    private final LobbyIdManager lobbyIdManager;
    public GamesManagerService(MoveValidator moveValidator, LobbyFactory lobbyFactory, LobbyIdManager gameIdManager) {
        this.moveValidator = moveValidator;
        this.lobbyFactory = lobbyFactory;
        this.lobbyIdManager =gameIdManager;
    }

    public void processMove(MovePieceRequest request) throws MovePieceException, JsonProcessingException {
        Lobby currentGame = this.findLobby(request.getIdPlayer());
        if (currentGame == null) {
            throw new MovePieceException("Game does not exist");
        }

        synchronized (currentGame) {
            currentGame.processMove(request);
        }
    }

    public Lobby processJoinRequest(JoinGameRequest request) {
        try {
            smallLock.lock();

            while (idFirstPlayer != -1 && idSecondPlayer != -1) {
                // Wait until a player slot becomes available
            }

            if (idFirstPlayer == -1) {
                idFirstPlayer = request.getIdPlayer();
            } else {
                idSecondPlayer = request.getIdPlayer();
            }

        } finally {
            smallLock.unlock();
        }

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Barrier problem: " + e.getMessage());
            return null;
        }

        if (request.getIdPlayer().equals(idFirstPlayer)) {
            Long idLobby = lobbyIdManager.getId();
            lobbyToBeCreated = lobbyFactory.createLobby(idLobby, idFirstPlayer, idSecondPlayer, moveValidator, 3);
            isGameCreated = true;
        }

        while (!isGameCreated) {
        }

        System.out.println("Game with id: " + lobbyToBeCreated.getIdLobby() + " was created");
        activeLobbies.put(request.getIdPlayer(), lobbyToBeCreated);

        if (idFirstPlayer.equals(request.getIdPlayer())) {
            idFirstPlayer = -1L;
        }
        if (idSecondPlayer.equals(request.getIdPlayer())) {
            idSecondPlayer = -1L;
        }
        return activeLobbies.get(request.getIdPlayer());
    }

    public Lobby findLobby(Long idPlayer) {
        return activeLobbies.get(idPlayer);
    }

    public void releaseLobby(Long idPlayer){
        Lobby lobby=this.findLobby(idPlayer);
        Long idPlayer1= lobby.getIdPlayer1();
        Long idPlayer2= lobby.getIdPlayer2();
        lobbyIdManager.freeId(lobby.getIdLobby());
        activeLobbies.remove(idPlayer1);
        activeLobbies.remove(idPlayer2);
    }
}
