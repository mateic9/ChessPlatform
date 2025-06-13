package org.example.chess_game_logic;




import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.authentification.AuthService;
import org.example.chess_game_logic.entities.*;
import org.example.chess_game_logic.requests.ForfeitRequest;
import org.example.chess_game_logic.requests.JoinGameRequest;
import org.example.chess_game_logic.requests.MovePieceRequest;
import org.example.chess_game_logic.requests.PromotePieceRequest;
import org.example.exceptions.*;
import org.example.entities.User;
import org.example.entities.UserRepo;
import org.example.websocket.WebSocketController;

//import org.example.websocker.SocketRegistry;
//import org.example.websocker.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/game")
public class GameController {

    private final GamesManagerService gameManager;
    private final AuthService authService;
    private final UserRepo userRepo;

    @Autowired
    private WebSocketController wsCont;



    @Autowired
    public GameController(GamesManagerService gameManager, UserRepo userRepo,AuthService authService) {
        this.gameManager = gameManager;
        this.userRepo = userRepo;
        this.authService=authService;
    }


        @PostMapping("/join")
        public ResponseEntity<Map<String, Object>> joinGame(@RequestBody JoinGameRequest request) {
            Map<String, Object> response = new HashMap<>();

            try {
                Optional<User> optionalUser = userRepo.findById(request.getIdPlayer());

                if (optionalUser.isEmpty()) {
                    response.put("success", false);
                    response.put("message", "User not found with ID: " + request.getIdPlayer());
                    response.put("board", null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Lobby game = gameManager.processJoinRequest(request);

                if (game != null) {
                    String boardFen = game.getMoveValidator().getBoard().getPiecePositionFen();
                    String positionOnlyFen = boardFen.split(" ")[0]; // Extract only piece positions
                    System.out.println(positionOnlyFen);
                    response.put("success", true);
                    response.put("message", "Player joined the game successfully");
                    response.put("board", positionOnlyFen);
                    return ResponseEntity.ok(response);
                } else {
                    response.put("success", false);
                    response.put("message", "Failed to create or join a game");
                    response.put("board", null);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }

            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "An error occurred: " + e.getMessage());
                response.put("board", null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }



    @PostMapping("/move-piece")
    public ResponseEntity<Map<String,Object>> movePiece(@RequestBody MovePieceRequest request) {
        Map<String, Object> response = new HashMap<>();
       try {

           gameManager.processMove(request);
           String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getPiecePositionFen();
           response.put("success",true);
           response.put("message","Move received");
           response.put("board",fenRep);
           sendOpponentChessboardConfig(request.getIdPlayer(),fenRep);
           System.out.println("Move made");
           return ResponseEntity.ok(response);
       }
       catch (MovePieceException e){
           response.put("success",false);
           response.put("message",e.getMessage());
           System.out.println("Move piece exception: "+e.getMessage());
           return ResponseEntity.ok(response);
       }
       catch(PromInfoNeededException e){
           String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getPiecePositionFen();
           response.put("success",true);
           response.put("message",e.getMessage());
           response.put("board",fenRep);
           System.out.println("Prom info exception: "+e.getMessage());
           return ResponseEntity.status(202).body(response);
       }
       catch (GameOverException e){
           String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getPiecePositionFen();
           response.put("success",true);
           response.put("message",e.getMessage());
           response.put("board",fenRep);
           sendOpponentChessboardConfig(request.getIdPlayer(),fenRep);
           sendOpponentGameOverMessage(request.getIdPlayer(), e.getMessage());
           sendCurrentPlayerGameOverMessage(request.getIdPlayer(),e.getMessage());
           System.out.println("Game Over exception: "+e.getMessage());
           gameManager.releaseLobby(request.getIdPlayer());
           return ResponseEntity.ok(response);
       }
       catch (JsonProcessingException e){
           response.put("success",false);
           response.put("message",e.getMessage());
           System.out.println("Json Proceed exception: "+e.getMessage());
           return   ResponseEntity.status(400).body(response);
       }
       catch(RunOutOfTimeException e){
           response.put("success",false);
           response.put("message",e.getMessage());
           sendOpponentGameOverMessage(request.getIdPlayer(), e.getMessage());
           sendCurrentPlayerGameOverMessage(request.getIdPlayer(),e.getMessage());
           System.out.println("Run out of time exception: "+e.getMessage());
           gameManager.releaseLobby(request.getIdPlayer());
           return ResponseEntity.ok(response);
       }
       catch(Exception e) {
           response.put("success",false);
           response.put("message",e.getMessage());
           System.out.println("Exception: "+e.getMessage());
           return   ResponseEntity.status(400).body(response);
       }

    }

    @PostMapping("/promote-piece")
    public ResponseEntity<Map<String,Object>> movePiece(@RequestBody PromotePieceRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {

            gameManager.findLobby(request.getIdPlayer()).processPromoteRequest(request);
            String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getPiecePositionFen();
            response.put("success",true);
            response.put("message","Move received");
            response.put("board",fenRep);
            return  ResponseEntity.ok(response);
        }
        catch (GameOverException e){
            String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getPiecePositionFen();
            response.put("success",true);
            response.put("message",e.getMessage());
            response.put("board",fenRep);
//            ws.sendOpponentChessBoard()
            return  ResponseEntity.ok(response);
        }
        catch (MovePieceException e) {

            response.put("success",false);
            response.put("message", e.getMessage());
            return  ResponseEntity.status(400).body(response);
        }

    }
    @PostMapping("/forfeit")
    public ResponseEntity<Map<String,Object>> forfeit(@RequestBody ForfeitRequest request){
        try {
            System.out.println("initiator forfeit: "+ request.getIdPlayer());
            GameResult result = gameManager.findLobby(request.getIdPlayer()).forfeit(request.getIdPlayer());
            sendForfeit(request.getIdPlayer(),result);
            gameManager.releaseLobby(request.getIdPlayer());
            return ResponseEntity.ok(result.toJson());
        }
        catch(Exception e){

            System.out.println(e.getMessage());
            Map<String,Object> body=new HashMap<String,Object>();
            body.put("succes",false);
            body.put("message",e.getMessage());
            return  ResponseEntity.status(500).body(body);
        }

    }
    private void sendOpponentChessboardConfig(Long playerId, String fen) {
        Lobby lobby = gameManager.findLobby(playerId);
        Long opponentId = playerId.equals(lobby.getIdPlayer1())
                ? lobby.getIdPlayer2()
                : lobby.getIdPlayer1();

        try {
//            socketRegistry.send(opponentId, Map.of("board", fen));
            wsCont.sendChessboard(opponentId,fen);
        } catch (Exception e) {

           System.out.println("Could not push board to {}"+opponentId);
           System.out.println(e.getMessage());
        }
    }
    private void sendForfeit(Long playerId,GameResult result){
        Lobby lobby = gameManager.findLobby(playerId);
        Long opponentId = playerId.equals(lobby.getIdPlayer1())
                ? lobby.getIdPlayer2()
                : lobby.getIdPlayer1();

        try {
//            socketRegistry.send(opponentId, Map.of("board", fen));
            wsCont.sendForfeit(opponentId,result.toJson());
        } catch (Exception e) {

            System.out.println("Could not push board to {}"+opponentId);
            System.out.println(e.getMessage());
        }
    }
    private void sendCurrentPlayerGameOverMessage(Long playerId, String reason){
        GameResult gameResult;
        if(reason.equals(ErrorMessage.RunOutOfTimeCurrentPlayer.get()))
            gameResult=new GameResult("Loss",reason);
        else
        if(reason.equals(ErrorMessage.Draw.get()))
            gameResult=new GameResult("Draw",reason);
        else
            gameResult=new GameResult("Win!",reason);

        try {

            wsCont.sendGameOverMessage(playerId,gameResult.toJson());
        } catch (Exception e) {

            System.out.println("Could not push board to {}"+playerId);
            System.out.println(e.getMessage());
        }
    }
    private void sendOpponentGameOverMessage(Long playerId, String reason){

        Lobby lobby = gameManager.findLobby(playerId);
        Long opponentId = playerId.equals(lobby.getIdPlayer1())
                ? lobby.getIdPlayer2()
                : lobby.getIdPlayer1();
       GameResult gameResult;
      if(reason.equals(ErrorMessage.RunOutOfTimeOpponentPlayer.get()))
          gameResult=new GameResult("Win",reason);
      else
          if(reason.equals(ErrorMessage.Draw.get()))
              gameResult=new GameResult("Draw",reason);
          else
              gameResult=new GameResult("Loss",reason);

        try {

            wsCont.sendGameOverMessage(opponentId,gameResult.toJson());
        } catch (Exception e) {

            System.out.println("Could not push board to {}"+opponentId);
            System.out.println(e.getMessage());
        }
    }

    
}
