package org.example.chess_game_logic;




import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.authentification.AuthService;
import org.example.chess_game_logic.requests.MovePieceRequest;
import org.example.chess_game_logic.requests.PromotePieceRequest;
import org.example.exceptions.GameOverException;
import org.example.exceptions.PromInfoNeededException;
import org.example.entities.User;
import org.example.entities.UserRepo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
                    String boardFen = game.getMoveValidator().getBoard().getFen();
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
           String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getFen();
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
           String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getFen();
           response.put("success",true);
           response.put("message",e.getMessage());
           response.put("board",fenRep);
           System.out.println("Prom info exception: "+e.getMessage());
           return ResponseEntity.status(202).body(response);
       }
       catch (GameOverException e){
           String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getFen();
           response.put("success",true);
           response.put("message",e.getMessage());
           response.put("board",fenRep);
           sendOpponentChessboardConfig(request.getIdPlayer(),fenRep);
           System.out.println("Game Over exception: "+e.getMessage());
           return ResponseEntity.status(202).body(response);
       }
       catch (JsonProcessingException e){
           response.put("success",false);
           response.put("message",e.getMessage());
           System.out.println("Json Proceed exception: "+e.getMessage());
           return   ResponseEntity.status(400).body(response);
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
            String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getFen();
            response.put("success",true);
            response.put("message","Move received");
            response.put("board",fenRep);
            return  ResponseEntity.ok(response);
        }
        catch (GameOverException e){
            String fenRep=gameManager.findLobby(request.getIdPlayer()).getMoveValidator().getBoard().getFen();
            response.put("success",true);
            response.put("message",e.getMessage());
            response.put("board",fenRep);
            return  ResponseEntity.ok(response);
        }
        catch (MovePieceException e) {

            response.put("success",false);
            response.put("message", e.getMessage());
            return  ResponseEntity.status(400).body(response);
        }

    }
    private void  sendOpponentChessboardConfig(Long idPlayer,String fen){
        Lobby lobby=gameManager.findLobby(idPlayer);
        Long opponentId;
        Map<String,Object> jsonBody=new HashMap<String,Object>();
        if(idPlayer.equals(lobby.getIdPlayer1()))
            opponentId=lobby.getIdPlayer2();
        else
            opponentId=lobby.getIdPlayer1();
        String url = "http://" + "127.0.0.1" + ":" +  authService.getUserAddressMap().get(opponentId) + "/receive-message";

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            jsonBody.put("board",fen);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(jsonBody, headers);

            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            System.err.println("Failed to notify client " + opponentId + ": " + e.getMessage());
        }

    }
}
