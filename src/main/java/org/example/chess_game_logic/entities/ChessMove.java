package org.example.chess_game_logic.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ChessMove {
    @Id
    @GeneratedValue(strategy =  GenerationType.SEQUENCE, generator = "chessmove_id_seq")
    @SequenceGenerator(name = "chessmove_id_seq", sequenceName = "chessmove_id_seq", allocationSize = 1)
    private Long id;

    private Long gameId;

    private Long playerId;
    private int moveNumber;
    private String fen;

    // getters/setters
}