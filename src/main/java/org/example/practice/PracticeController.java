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
            int idx= updatedGame.getCurFenIdx();
            MoveRecord lastMove=updatedGame.getHistory().get(idx);
            jsonBody.put("fen1",lastMove.getFenBeforeMove());
            jsonBody.put("fen2", lastMove.getFenAfterMove());
            System.out.println("fen: "+updatedGame.getFen());
            return ResponseEntity.ok(jsonBody);

        }catch (GameOverException exc) {
            System.out.println(exc);
            PracticeGame updatedGame=practiceService.getGames().get(request.getUserId());
            int idx= updatedGame.getCurFenIdx();
            MoveRecord lastMove=updatedGame.getHistory().get(idx);
            jsonBody.put("message",exc.getMessage());
            jsonBody.put("fen1",lastMove.getFenBeforeMove());
            jsonBody.put("fen2",lastMove.getFenAfterMove());
            return  ResponseEntity.status(201).body(jsonBody);

        }
        catch (InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Move handling failed."));
        }
    }
//    @PostMapping("/undo_move")
//    public ResponseEntity<?> undoMove(@RequestBody UndoMoveRequest request){
//        Map<String,Object> jsonBody=new HashMap<String,Object>();
//        try{
//            PracticeGame updatedGame=practiceService.undoMove(request);
//            int idx= updatedGame.getCurFenIdx()+1;
//            MoveRecord lastMove=updatedGame.getHistory().get(idx);
//            jsonBody.put("message","Move undone");
////            jsonBody.put("fen",game.getFen());
//
//            jsonBody.put("message","Move retrieved");
////            jsonBody.put("fen",game.getFen());
//            jsonBody.put("fen1",lastMove.getFenAfterMove());
//            jsonBody.put("fen2",lastMove.getFenBeforeMove());
//            return ResponseEntity.ok(jsonBody);
//        }
//
//        catch (Exception e){
//            jsonBody.put("message",e.getMessage());
//            return ResponseEntity.status(400).body(jsonBody);
//        }
//    }
@PostMapping("/undo_move")
public ResponseEntity<?> undoMove(@RequestBody UndoMoveRequest request) {
    Map<String, Object> jsonBody = new HashMap<>();
    try {
        PracticeGame updatedGame = practiceService.undoMove(request);

        // After undoLastMove(), curFenIdx points to the position we're now at
        // For the animation, we want to show moving from where we were back to where we are now
        int currentIdx = updatedGame.getCurFenIdx();

        // The move we undid should be at currentIdx + 2 (since we went back 2 positions)
        int undonePlayerMoveIdx = currentIdx + 2;
        int undoneAiMoveIdx = currentIdx + 1;

        if (undonePlayerMoveIdx < updatedGame.getHistory().size()) {
            // Show the undo animation: from the undone position back to current position
            MoveRecord undonePlayerMove = updatedGame.getHistory().get(undonePlayerMoveIdx);

            // fen1: position after the moves that were undone
            // fen2: current position (before those moves)
            jsonBody.put("message", "Move undone");
            jsonBody.put("fen1", undonePlayerMove.getFenAfterMove()); // Where we were
            jsonBody.put("fen2", updatedGame.getFen()); // Where we are now
        } else {
            jsonBody.put("message", "Move undone");
            jsonBody.put("fen1", updatedGame.getFen());
            jsonBody.put("fen2", updatedGame.getFen());
        }

        return ResponseEntity.ok(jsonBody);
    } catch (Exception e) {
        jsonBody.put("message", e.getMessage());
        return ResponseEntity.status(400).body(jsonBody);
    }
}
    @PostMapping("/next_move")
    public ResponseEntity<?> nextMove(@RequestBody NextMoveRequest request){
        Map<String,Object> jsonBody=new HashMap<String,Object>();
        try{
            PracticeGame updatedGame=practiceService.nextMove(request);
            int idx= updatedGame.getCurFenIdx();
            MoveRecord lastMove=updatedGame.getHistory().get(idx);
            jsonBody.put("message","Move retrieved");
//            jsonBody.put("fen",game.getFen());
            jsonBody.put("fen1",lastMove.getFenBeforeMove());
            jsonBody.put("fen2",lastMove.getFenAfterMove());
            return ResponseEntity.ok(jsonBody);
        }

        catch (Exception e){
            jsonBody.put("message",e.getMessage());
            return ResponseEntity.status(400).body(jsonBody);
        }
    }
}

