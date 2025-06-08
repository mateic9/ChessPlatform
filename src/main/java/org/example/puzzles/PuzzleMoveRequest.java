package org.example.puzzles;



import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PuzzleMoveRequest {
    private Long playerId;
    private String puzzleId;
    private String move;   // e.g., "g4f2"
    private int moveIndex; // e.g., 0
}
