package org.example.chess_game_logic.entities;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/results")
@CrossOrigin(origins = "*") // allow all origins (adjust as needed)
public class GameResultController {

    private final GameResultEntityRepository gameResultRepo;

    public GameResultController(GameResultEntityRepository gameResultRepo) {
        this.gameResultRepo = gameResultRepo;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<List<Map<String, Object>>> getResultsForPlayer(@PathVariable Long playerId) {
        List<GameResultEntity> results = gameResultRepo.findByPlayerId(playerId);
        System.out.println("Jocuri gasite: "+results.size()+ " pentru id: "+playerId);
        // You can shape the response to only include what's needed
        List<Map<String, Object>> response = results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("gameId", result.getGameId());
                    map.put("result", result.getResult());
                    map.put("reason", result.getReason());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
