package org.example.chess_game_logic.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChessGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long playerWhiteId;
    private Long playerBlackId;
    public ChessGame(Long id1,Long id2){
        this.playerWhiteId=id1;
        this.playerBlackId=id2;
    }
}