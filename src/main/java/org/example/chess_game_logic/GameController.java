package org.example.chess_game_logic;




import org.example.entities.PromInfoNeededException;
import org.example.entities.User;
import org.example.entities.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GamesManagerService gameManager;
    private final UserRepo userRepo;

    @Autowired
    public GameController(GamesManagerService gameManager, UserRepo userRepo) {
        this.gameManager = gameManager;
        this.userRepo = userRepo;
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinGame(@RequestBody JoinGameRequest request) {
        Optional<User> optionalUser = userRepo.findById(request.getIdPlayer());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + request.getIdPlayer());
        }
        Lobby game=gameManager.processJoinRequest(request);
        if(game!=null)
         return ResponseEntity.ok("Player joined with id: " + request.getIdPlayer()+" joined the game "+game.getIdGame());
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Nu s-a putut crea jocul");
    }

    @PostMapping("/move-piece")
    public ResponseEntity<String> movePiece(@RequestBody MovePieceRequest request) {

       try {

           gameManager.processMove(request);
           return ResponseEntity.ok("Move received");
       }
       catch (MovePieceException e){
           return ResponseEntity.badRequest().body(e.getMessage());
       }
       catch(PromInfoNeededException e){
           return ResponseEntity.status(202).body(e.getMessage());
       }
       catch(Exception e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(e.getMessage());
       }
    }

    @PostMapping("/promote-piece")
    public ResponseEntity<String> movePiece(@RequestBody PromotePieceRequest request) {
        try {

            gameManager.findGame(request.getIdPlayer()).processPromoteRequest(request);
            return ResponseEntity.ok("Promotion done!");
        }
        catch (MovePieceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
