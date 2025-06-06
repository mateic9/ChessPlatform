package org.example.puzzles;

import org.example.puzzles.entities.*;

import org.example.authentification.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/puzzle")
@CrossOrigin(origins = "*")
public class PuzzleController {

    @Autowired
    private AuthService authService;
    @Autowired private PuzzleRepository puzzleRepository;
    @Autowired private PlayerPuzzleProgressRepository progressRepository;
    @Autowired private RestTemplate restTemplate;

    private final String lichessDailyUrl = "https://lichess.org/api/puzzle/daily";

    @GetMapping("/daily")
    public ResponseEntity<?> getDailyPuzzle(@RequestParam Long idPlayer) {
        try {
            if (!authService.isUserLogged(idPlayer)) {
                throw new Exception("Login required.");
            }

            ResponseEntity<Map<String, Object>> apiResponse = restTemplate.exchange(
                    lichessDailyUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> body = apiResponse.getBody();
            if (body == null || !body.containsKey("puzzle")) throw new Exception("Invalid API response");

            Map<String, Object> puzzleMap = (Map<String, Object>) body.get("puzzle");
            String puzzleId = (String) puzzleMap.get("id");
            Map<String, Object> gameMap = (Map<String, Object>) body.get("game");
            String pgn = (String) gameMap.get("pgn"); // double check actual structure
            System.out.println("puzzle pgn:"+pgn);
            List<String> solution = (List<String>) puzzleMap.get("solution");

            puzzleRepository.save(new Puzzle(puzzleId, pgn, solution));
            System.out.println("No error getting daily puzzle");
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "puzzleId", puzzleId,
                    "pgn", pgn
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/move")
    public ResponseEntity<?> validateMove(@RequestBody PuzzleMoveRequest request) {
        try {
            Map<String,Object> jsonBody=new HashMap<String,Object>();
            String puzzleId= request.getPuzzleId();
            Long playerId= request.getPlayerId();
            if (!authService.isUserLogged(playerId)) {
                throw new Exception("Login required.");
            }

            Optional<Puzzle> optPuzzle = puzzleRepository.findById(puzzleId);
            if (optPuzzle.isEmpty()) {
                throw new Exception("Puzzle not found.");
            }
          List<String> solutionSteps=optPuzzle.get().getSolution();
            int idxPlayerMove= request.getMoveIndex();
            int idxDBMove=2*idxPlayerMove;
            if(idxDBMove>=solutionSteps.size())
                throw new Exception("Idx of move out of range");
            if(request.getMove().equals(solutionSteps.get(idxDBMove)))
            {
                if(idxDBMove==(solutionSteps.size()-1))
                {
                    System.out.println("Solved puzzle");
                  jsonBody.put("succes",true);
                 return  ResponseEntity.ok(jsonBody);
                }
                else{
                    System.out.println("Opponent plays: "+solutionSteps.get(idxDBMove+1));
                    jsonBody.put("succes",true);
                    jsonBody.put("oppMove",solutionSteps.get(idxDBMove+1));
                    return ResponseEntity.status(202).body(jsonBody);
                }
            }
            else
                throw new Exception("Wrong Move!");




        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
