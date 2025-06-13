package org.example.practice;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import lombok.Getter;
import org.example.practice.requests.GameInitRequest;
import org.example.practice.requests.MoveRequest;
import org.example.practice.requests.NextMoveRequest;
import org.example.practice.requests.UndoMoveRequest;
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

    @Getter
    private final Map<Integer, PracticeGame> games = new ConcurrentHashMap<Integer,PracticeGame>();
    private final AtomicInteger idGenerator = new AtomicInteger();
    @Autowired
    private ChessEngineService chessEngine;
    @Autowired
    private WebSocketController wsCont;

    public PracticeGame handlePlayerMove(MoveRequest req) throws InvalidMoveException {
        PracticeGame game = games.get(req.getUserId());
        if (game == null) throw new IllegalArgumentException("Game not found.");
        System.out.println("check move");
        System.out.println("from "+req.getFrom()+" to "+req.getTo());
        String newFen = chessEngine.makeMove(game.getFen(), req.getFrom().toUpperCase(), req.getTo().toUpperCase(),PlayerType.Human);
        System.out.println("after checking move");
        game.applyMove(req.getFrom(), req.getTo(), newFen);
        chessEngine.checkIsGameOver(game.getFen(),PlayerType.Human);
//        sendFenToClient(game);
        triggerAiMove(game);
        chessEngine.checkIsGameOver(game.getFen(),PlayerType.AI);
//        System.out.println(chessEngine.ge);
        return game;
    }

    public PracticeGame initializeGame(GameInitRequest request) throws Exception{
        String color=request.getColor();
        if(!color.equals("WHITE")&&!color.equals("BLACK"))
            throw new Exception("Problem with color parameter");
        int userId = idGenerator.incrementAndGet();
        PracticeGame game = new PracticeGame(userId, request.getFen(), request.getColor(), request.getDifficulty());
        games.put(userId, game);
//        sendFenToClient(game);
        System.out.println(color);
        Board board=new  Board();
        board.loadFromFen(request.getFen());

        Side side=board.getSideToMove();
        if((color.equals("BLACK")&&side==Side.WHITE)||(color.equals("WHITE")&& side==Side.BLACK))
            triggerAiMove(game);
        return game;
    }
    public PracticeGame undoMove(UndoMoveRequest request) throws Exception {
        PracticeGame game = games.get(request.getUserId());
        if (game == null) throw new IllegalArgumentException("Game not found.");
        game.undoLastMove();
        return game;
    }

    public PracticeGame nextMove(NextMoveRequest request) throws Exception {
        PracticeGame game = games.get(request.getUserId());
        if (game == null) throw new IllegalArgumentException("Game not found.");
        game.nextMove();
        return game;
    }




    private void sendFenToClient(PracticeGame game) {
        String fen = game.getFen();
//       wsCont.sendFenToUser(game.getUserId(), fen, game.isPlayerTurn());
    }

    private void triggerAiMove(PracticeGame game) {
        try {
            // Only trigger if it's NOT the player's turn


            // Load board from current FEN
            Board board = new Board();
            board.loadFromFen(game.getFen());

            // Generate legal moves
            List<Move> legalMoves = MoveGenerator.generateLegalMoves(board);
            if (legalMoves.isEmpty()) return; // Game over

            // Pick random move
            Move aiMove = legalMoves.get(new Random().nextInt(legalMoves.size()));
            String from= String.valueOf(aiMove.getFrom().getFile());
            System.out.println(from);
            // Apply the move
            board.doMove(aiMove);

            // Update game state
            String newFen = board.getFen();
            game.applyMove(aiMove.getFrom().toString(), aiMove.getTo().toString(), newFen);

            // Send updated board to client
//            sendFenToClient(game);

        } catch (Exception e) {
            e.printStackTrace(); // Log error for debugging
        }
    }


}
