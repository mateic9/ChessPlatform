package org.example.practice;

import org.example.exceptions.GameOverException;
import org.example.practice.requests.GameInitRequest;
import org.example.practice.requests.MoveRequest;
import org.example.practice.requests.UndoMoveRequest;
import org.example.practice.requests.NextMoveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
            Map<String,Object> jsonBody=new HashMap<String,Object>();
            jsonBody.put("in_practice_id",game.getUserId());
            jsonBody.put("fen",game.getFen());
            return ResponseEntity.ok().body(jsonBody);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to init game."));
        }
    }

    @PostMapping("/make_move")
    public ResponseEntity<?> makeMove(@RequestBody MoveRequest request) {
        Map<String,Object> jsonBody=new HashMap<String,Object>();
        try {

            PracticeGame updatedGame = practiceService.handlePlayerMove(request);
            jsonBody.put("message","Move proceeded");
            jsonBody.put("fen",updatedGame.getFen());
            System.out.println("fen: "+updatedGame.getFen());
            return ResponseEntity.ok(jsonBody);

        }catch (GameOverException exc) {
            System.out.println(exc);
            PracticeGame game=practiceService.getGames().get(request.getUserId());
            jsonBody.put("message",exc.getMessage());
            jsonBody.put("fen",game.getFen());
            return  ResponseEntity.status(201).body(jsonBody);

        }
        catch (InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Move handling failed."));
        }
    }
    @PostMapping("/undo_move")
    public ResponseEntity<?> undoMove(@RequestBody UndoMoveRequest request){
        Map<String,Object> jsonBody=new HashMap<String,Object>();
        try{
            PracticeGame game=practiceService.undoMove(request);
            jsonBody.put("message","Move undone");
            jsonBody.put("fen",game.getFen());
            return ResponseEntity.ok(jsonBody);
        }

        catch (Exception e){
            jsonBody.put("message",e.getMessage());
            return ResponseEntity.status(400).body(jsonBody);
        }
    }
    @PostMapping("/next_move")
    public ResponseEntity<?> nextMove(@RequestBody NextMoveRequest request){
        Map<String,Object> jsonBody=new HashMap<String,Object>();
        try{
            PracticeGame game=practiceService.nextMove(request);
            jsonBody.put("message","Move retrieved");
            jsonBody.put("fen",game.getFen());
            return ResponseEntity.ok(jsonBody);
        }

        catch (Exception e){
            jsonBody.put("message",e.getMessage());
            return ResponseEntity.status(400).body(jsonBody);
        }
    }
}

