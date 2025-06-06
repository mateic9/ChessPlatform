package org.example.puzzles.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "player_puzzle_progress")
@NoArgsConstructor
public class PlayerPuzzleProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long playerId;
    private String puzzleId;
    private int currentIndex; // next expected move

    private boolean solved;

    // equals/hashCode on playerId+puzzleId if needed
}
