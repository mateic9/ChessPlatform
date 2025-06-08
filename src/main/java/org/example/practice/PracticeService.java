package org.example.practice;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import org.example.websocket.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PracticeService {

    private final Map<Integer, PracticeGame> games = new ConcurrentHashMap<Integer,PracticeGame>();
    private final AtomicInteger idGenerator = new AtomicInteger();
    @Autowired
    private ChessEngineService chessEngine;
    @Autowired
    private WebSocketController wsCont;

    public PracticeGame handlePlayerMove(MoveRequest req) throws InvalidMoveException {
        PracticeGame game = games.get(req.getUserId());
        if (game == null) throw new IllegalArgumentException("Game not found.");

        String newFen = chessEngine.makeMove(game.getFen(), req.getFrom(), req.getTo());
        game.applyMove(req.getFrom(), req.getTo(), newFen);
        sendFenToClient(game);
        triggerAiMove(game);
        return game;
    }

    public PracticeGame initializeGame(GameInitRequest request) {
        int userId = idGenerator.incrementAndGet();
        PracticeGame game = new PracticeGame(userId, request.getFen(), request.getColor(), request.getDifficulty());
        games.put(userId, game);
        sendFenToClient(game);
        return game;
    }



    private void sendFenToClient(PracticeGame game) {
        String fen = game.getFen();
       wsCont.sendFenToUser(game.getUserId(), fen, game.isPlayerTurn());
    }

    private void triggerAiMove(PracticeGame game) {
        try {
            // Only trigger if it's NOT the player's turn
            if (game.isPlayerTurn()) return;

            // Load board from current FEN
            Board board = new Board();
            board.loadFromFen(game.getFen());

            // Generate legal moves
            List<Move> legalMoves = MoveGenerator.generateLegalMoves(board);
            if (legalMoves.isEmpty()) return; // Game over

            // Pick random move
            Move aiMove = legalMoves.get(new Random().nextInt(legalMoves.size()));

            // Apply the move
            board.doMove(aiMove);

            // Update game state
            String newFen = board.getFen();
            game.applyMove(aiMove.getFrom().toString(), aiMove.getTo().toString(), newFen);

            // Send updated board to client
            sendFenToClient(game);

        } catch (Exception e) {
            e.printStackTrace(); // Log error for debugging
        }
    }
}
