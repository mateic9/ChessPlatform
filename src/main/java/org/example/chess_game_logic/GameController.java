package org.example.chess_game_logic;




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


        return ResponseEntity.ok("Player joined: " + request.getIdPlayer());
    }

    @PostMapping("/move-piece")
    public ResponseEntity<String> movePiece(@RequestBody MovePieceRequest request) {

       try {
           gameManager.processMove(request);
           return ResponseEntity.ok("Move received");
       }
       catch(Exception e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(e.getMessage());
       }
    }
}
