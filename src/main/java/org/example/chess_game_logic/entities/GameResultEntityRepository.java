package org.example.chess_game_logic.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameResultEntityRepository extends JpaRepository<GameResultEntity, Long> {
    List<GameResultEntity> findByPlayerId(Long playerId);
}