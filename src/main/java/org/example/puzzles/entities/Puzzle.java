package org.example.puzzles.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "puzzles")
@AllArgsConstructor
@NoArgsConstructor
public class Puzzle {

    @Id
    private String id;

    private String pgn;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "puzzle_solutions",
            joinColumns = @JoinColumn(name = "puzzle_id")
    )
    @Column(name = "solution_move")
    private List<String> solution;
//    public Puzzle(String id, String pgn, List<String> solution) {
//        this.id = id;
//        this.pgn = pgn;
//        this.solution = solution;
//    }

    // add getters, setters
}
