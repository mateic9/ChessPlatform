package org.example.chess_game_logic;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GamesManagerService {
    private final Queue<PlayerWaiting> waitingPlayers = new ConcurrentLinkedQueue<>();
    private final Map<Long, ChessGame> activeGames = new ConcurrentHashMap<>();
    private final AtomicLong gameIdGenerator = new AtomicLong(1);

    /**
     * Player joins matchmaking queue and waits for opponent
     * @param playerId ID of the player joining the queue
     * @return ChessGame if match is found immediately, null if player is in waiting queue
     */
    public ChessGame joinMatchmaking(Long playerId) {
        // Check if player is already in a game
        ChessGame existingGame = findGame(playerId);
        if (existingGame != null) {
            return existingGame;
        }

        // Check if player is already in the waiting queue
        for (PlayerWaiting waiting : waitingPlayers) {
            if (Objects.equals(waiting.getPlayerId(), playerId)) {
                return null; // Already waiting
            }
        }

        // Try to match with waiting player
        PlayerWaiting opponent = waitingPlayers.poll();
        if (opponent != null) {
            // Create a new game with the opponent
            Long gameId = gameIdGenerator.getAndIncrement();
            ChessGame newGame = new ChessGame(gameId, opponent.getPlayerId(), playerId);
            activeGames.put(gameId, newGame);
            return newGame;
        } else {
            // No opponent available, add to waiting queue
            waitingPlayers.add(new PlayerWaiting(playerId));
            return null;
        }
    }

    /**
     * Check if match is found for a waiting player
     * @param playerId ID of the waiting player
     * @return ChessGame if match is found, null otherwise
     */
    public ChessGame checkMatchmakingStatus(Long playerId) {
        ChessGame game = findGame(playerId);
        if (game != null) {
            return game;
        }

        // Player still waiting, no match yet
        return null;
    }

    /**
     * Cancel matchmaking for a waiting player
     * @param playerId ID of the player to remove from waiting queue
     * @return true if player was removed from queue, false if not found
     */
    public boolean cancelMatchmaking(Long playerId) {
        return waitingPlayers.removeIf(waiting ->
                Objects.equals(waiting.getPlayerId(), playerId));
    }

    /**
     * Process a move for a chess game
     * @param request Move request containing player ID and move details
     * @throws MovePieceException If the move is invalid or game doesn't exist
     */
    public void processMove(MovePieceRequest request) throws MovePieceException {
        ChessGame currentGame = this.findGame(request.getIdPlayer());
        if (currentGame == null) {
            throw new MovePieceException("Nu exista acest joc");
        }

        // Using the game object's own synchronization
        currentGame.processMove(request);
    }

    /**
     * Find a game that a player is participating in
     * @param playerId ID of the player
     * @return ChessGame if found, null otherwise
     */
    public ChessGame findGame(Long playerId) {
        for (ChessGame game : activeGames.values()) {
            if (Objects.equals(playerId, game.getIdPlayer1()) ||
                    Objects.equals(playerId, game.getIdPlayer2())) {
                return game;
            }
        }
        return null;
    }

    /**
     * End a chess game and remove it from active games
     * @param gameId ID of the game to end
     * @param winnerId ID of the winning player (null for draw)
     * @return true if game was found and removed, false otherwise
     */
    public boolean endGame(Long gameId, Long winnerId) {
        ChessGame game = activeGames.remove(gameId);
        if (game != null) {
            // Additional logic for game ending can be added here
            // e.g., updating player stats, storing game result, etc.
            return true;
        }
        return false;
    }

    /**
     * Helper class to store waiting player information
     * Could be extended with timestamps for timeout handling, ELO for matchmaking, etc.
     */
    private static class PlayerWaiting {
        private final Long playerId;
        private final long joinTime;

        public PlayerWaiting(Long playerId) {
            this.playerId = playerId;
            this.joinTime = System.currentTimeMillis();
        }

        public Long getPlayerId() {
            return playerId;
        }

        public long getJoinTime() {
            return joinTime;
        }
    }
}


//package org.example.chess_game_logic;
//
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Objects;
//import java.util.Queue;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.List;
//@Service
//public class GamesManagerService {
//    private final Queue<String> waitingPlayers = new ConcurrentLinkedQueue<>();
//    private final List<ChessGame> chessGamesList;
//   GamesManagerService(){
//       chessGamesList=new ArrayList<ChessGame>();
//   }
//   void processMove(MovePieceRequest request) throws MovePieceException{
//       ChessGame currentGame=this.findGame(request.getIdPlayer());
//       if(currentGame==null)
//           throw new MovePieceException("Nu exista acest joc");
//       synchronized (currentGame){
//
//               currentGame.processMove(request);
//
//
//       }
//
//   }
//
//   ChessGame findGame(Long idPlayer){
//       synchronized (chessGamesList){
//           for(ChessGame game: chessGamesList )
//               if(Objects.equals(idPlayer, game.getIdPlayer1()) || Objects.equals(idPlayer, game.getIdPlayer2()))
//                   return game;
//       }
//       return null;
//   }
//}
