package org.example.practice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/practice")
public class PracticeController {

    @Autowired
    private PracticeService practiceService;

    @PostMapping("/init_game")
    public ResponseEntity<?> initGame(@RequestBody GameInitRequest request) {
        try {
            PracticeGame game = practiceService.initializeGame(request);
            return ResponseEntity.ok().body(Map.of("in_practice_id", game.getUserId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to init game."));
        }
    }

    @PostMapping("/make_move")
    public ResponseEntity<?> makeMove(@RequestBody MoveRequest request) {
        try {
            PracticeGame updatedGame = practiceService.handlePlayerMove(request);
            return ResponseEntity.ok().body(Map.of("message", "Move processed."));
        } catch (InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Move handling failed."));
        }
    }
}

