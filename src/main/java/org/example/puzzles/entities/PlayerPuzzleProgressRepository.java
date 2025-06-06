package org.example.puzzles.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerPuzzleProgressRepository extends JpaRepository<PlayerPuzzleProgress, Long> {
    Optional<PlayerPuzzleProgress> findByPlayerIdAndPuzzleId(Long playerId, String puzzleId);
}
