
package org.example.practice;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PracticeGame {
    private final int userId;
    private final String difficulty;
    private final String playerColor;
    private final List<MoveRecord> history = new ArrayList<>();
    private String fen;
    private int curFenIdx;

    public PracticeGame(int userId, String fen, String color, String difficulty) {
        this.userId = userId;
        this.fen = fen;
        this.playerColor = color;
        this.difficulty = difficulty;
        curFenIdx = -1;
    }

    public void applyMove(String from, String to, String updatedFen) {
        // If we're in the middle of history, discard future moves
        if (curFenIdx < history.size() - 1) {
            history.subList(curFenIdx + 1, history.size()).clear();
        }

        history.add(new MoveRecord(from, to, this.fen, updatedFen));
        this.fen = updatedFen;
        curFenIdx++;
        System.out.println("Move applied. New curFenIdx: " + curFenIdx + ", History size: " + history.size());
    }

    public void undoLastMove() throws Exception {
        // Need at least 2 moves to undo (player + AI)
        if (curFenIdx < 1) {
            throw new Exception("No moves to undo (need at least player + AI move)");
        }

        System.out.println("Undoing from curFenIdx: " + curFenIdx + " to " + (curFenIdx - 2));

        // Go back 2 moves (undo AI move + player move)
        curFenIdx -= 2;

        // Set FEN to the position before the player's move
        if (curFenIdx >= 0) {
            MoveRecord targetMove = history.get(curFenIdx);
            this.fen = targetMove.getFenAfterMove();
        } else {
            // Back to initial position
            MoveRecord firstMove = history.get(0);
            this.fen = firstMove.getFenBeforeMove();
        }

        System.out.println("After undo - curFenIdx: " + curFenIdx + ", FEN: " + this.fen);
    }

    public void nextMove() throws Exception {
        if (curFenIdx >= history.size() - 1) {
            throw new Exception("No next move available");
        }

        // Move forward one step
        curFenIdx++;
        MoveRecord next = history.get(curFenIdx);
        this.fen = next.getFenAfterMove();

        // If there's another move and we're doing player+AI pairs, move forward again
        if (curFenIdx < history.size() - 1) {
            curFenIdx++;
            MoveRecord second = history.get(curFenIdx);
            this.fen = second.getFenAfterMove();
        }

        System.out.println("After next move - curFenIdx: " + curFenIdx + ", FEN: " + this.fen);
    }
}
//package org.example.practice;
//
//import lombok.Getter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//public class PracticeGame {
//    private final int userId;
//    private final String difficulty;
//    private final String playerColor;
//    private final List<MoveRecord> history = new ArrayList<>();
//    private String fen;
//    private int curFenIdx;
//    private boolean playerTurn;
//
//    public PracticeGame(int  userId, String fen, String color, String difficulty) {
//        this.userId = userId;
//        this.fen = fen;
//        this.playerColor = color;
//        this.difficulty = difficulty;
//        this.playerTurn = "white".equalsIgnoreCase(color);
//        curFenIdx=-1;
//    }
//
//    public void applyMove(String from, String to, String updatedFen) {
//        // If we're in the middle of history, discard future moves
//        if (curFenIdx < history.size()) {
//            history.subList(curFenIdx+1, history.size()).clear();
//        }
//
//        history.add(new MoveRecord(from, to, this.fen, updatedFen));
//        this.fen = updatedFen;
//        this.playerTurn = !playerTurn;
//        curFenIdx++;
//        System.out.println("Mutare idx: "+curFenIdx);
//    }
//
//    public void undoLastMove() throws Exception {
//        if (curFenIdx < 1)
//            throw new Exception("No move to undo");
//        System.out.println("from idx: "+curFenIdx+" to "+ (curFenIdx-2));
//        curFenIdx -= 2;
//        MoveRecord previous = history.get(curFenIdx);
//        this.fen = previous.getFenAfterMove();
//        this.playerTurn = "white".equalsIgnoreCase(this.playerColor) == (curFenIdx % 2 == 0);
//    }
//
//    public void nextMove() throws Exception {
//        if (curFenIdx  >= history.size())
//            throw new Exception("No next move available");
//
//        // MoveRecord at [curFenIdx] is the next half-move (player or AI)
//        MoveRecord next = history.get(curFenIdx);
//        this.fen = next.getFenAfterMove();
//        curFenIdx++;
//
//        // Apply second half-move if exists (player+AI as a pair)
//        if (curFenIdx < history.size()) {
//            MoveRecord second = history.get(curFenIdx);
//            this.fen = second.getFenAfterMove();
//            curFenIdx++;
//        }
//
//        this.playerTurn = "white".equalsIgnoreCase(this.playerColor) == (curFenIdx % 2 == 0);
//    }
//
//
//
//
//
//}
//
