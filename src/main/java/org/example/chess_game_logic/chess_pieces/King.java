package org.example.chess_game_logic.chess_pieces;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
@Getter

public class King implements PieceInterface {
    private final int value = 1000;
    @Setter
    private boolean moved;
    private final Color color;
    private final List<ChessMoveType> moveTypes = Arrays.asList(
            ChessMoveType.Diagonal,
            ChessMoveType.Horizontal,
            ChessMoveType.Vertical
    );

    King(Color color) {
        this.color = color;
        this.moved = false;
    }

    @Override
    public String toString() {
        return "King " + color;
    }

    @Override
    public boolean canMove(Position curPosition, Position destPosition, ChessMoveType moveType, Board board) {
        if (!moveTypes.contains(moveType)) return false;

        if (moveType == ChessMoveType.Vertical)
            return canMoveLinear(curPosition, destPosition, moveType, board);
        else if (moveType == ChessMoveType.Horizontal)
            return canMoveHorizontal(curPosition, destPosition, moveType, board);
        else if (moveType == ChessMoveType.Diagonal)
            return canMoveLinear(curPosition, destPosition, moveType, board);

        return false;
    }

    private boolean canMoveLinear(Position curPosition, Position destPosition, ChessMoveType moveType, Board board) {
        int offsetX = moveType.getOffsetX(curPosition, destPosition);
        int offsetY = moveType.getOffsetY(curPosition, destPosition);

        int curX = curPosition.getX();
        int curY = curPosition.getY();
        int destX = destPosition.getX();
        int destY = destPosition.getY();

        curX += offsetX;
        curY += offsetY;

        Position newPos = new Position(curX, curY);
        if (!newPos.equals(destPosition)) return false;

        PieceInterface encounteredPiece = board.getPieceAt(destPosition);
        return encounteredPiece == null ||
                (!(encounteredPiece instanceof King) && encounteredPiece.getColor() != color);
    }

    public boolean canMoveHorizontal(Position curPosition, Position destPosition, ChessMoveType moveType, Board board) {
        int difY = Math.abs(curPosition.getY() - destPosition.getY());
        if (difY == 1)
            return canMoveLinear(curPosition, destPosition, moveType, board);

        return false;
    }

    Position getPositionCastle(Position kingPos, Position destPos, ChessMoveType moveType,Board board) {
        if(moveType!=ChessMoveType.Horizontal)
            return null;
        int difY = Math.abs(kingPos.getY() - destPos.getY());
        if (difY != 2 || this.isMoved())
            return null;


        int row = kingPos.getX();
        int direction = destPos.getY() > kingPos.getY() ? 1 : -1;
        int rookCol = direction == 1 ? 7 : 0;

        // Check Rook at the end
        Position rookPos = new Position(row, rookCol);
        PieceInterface rook = board.getPieceAt(rookPos);

        if (!(rook instanceof Rook) || rook.getColor() != color || ((Rook) rook).isMoved())
            return null;

        // Check that all spaces between king and rook are empty
        for (int y = kingPos.getY() + direction; y != rookCol; y += direction) {
            Position between = new Position(row, y);
            if (board.getPieceAt(between) != null) return null;
        }

        // (Optional) Add check verification for squares the king passes through

        return rookPos;
    }
    @Override
    public boolean canCapture(Position curPosition, Position destPosition, ChessMoveType moveType, Board board){
        if(!moveTypes.contains(moveType))
            return false;
       return canMoveLinear(curPosition,destPosition,moveType,board);
    }
    public boolean isInCheck(Board board) {
        Position kingPos = board.getKingPozMap().get(color);  // assumes this King is the current player's king

        List<ChessMoveType> directions = Arrays.asList(
                ChessMoveType.Vertical,
                ChessMoveType.Horizontal,
                ChessMoveType.Diagonal
        );

        for (ChessMoveType dir : directions) {
            int[][] offsets = getOffsetsForDirection(dir);

            int[] xOffsets = offsets[0];
            int[] yOffsets = offsets[1];

            for (int i = 0; i < xOffsets.length; i++) {
                int dx = xOffsets[i];
                int dy = yOffsets[i];
                int x = kingPos.getX() + dx;
                int y = kingPos.getY() + dy;

                while (board.isOnBoard(new Position(x, y))) {
                    Position pos = new Position(x, y);
                    PieceInterface piece = board.getPieceAt(pos);
                    if (piece != null) {
                        if (piece.getColor() != color &&
                                piece.canCapture(pos, kingPos, dir, board)) {
                            System.out.println(piece + "attacks "+this);
                            return true;  // King is in check
                        } else {
                            break;  // blocked by own piece or wrong opponent piece
                        }
                    }
                    x += dx;
                    y += dy;
                }
            }
        }
       System.out.println("all checks made!");
        // Add knight check
        int[][] knightMoves = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        for (int[] move : knightMoves) {
            Position knightPos = new Position(kingPos.getX() + move[0], kingPos.getY() + move[1]);
            if (board.isOnBoard(knightPos)) {
                PieceInterface p = board.getPieceAt(knightPos);
                if (p != null && p.getColor() != color && p instanceof Knight) {
                    return true;
                }
            }
        }
//
//        // Add pawn check (simplified logic, assuming white moves up and black moves down)
//        int pawnDir = (color == Color.WHITE) ? -1 : 1;
//        int[] pawnOffsets = {-1, 1};
//        for (int dy : pawnOffsets) {
//            Position checkPawn = new Position(kingPos.getX() + pawnDir, kingPos.getY() + dy);
//            if (board.isOnBoard(checkPawn)) {
//                PieceInterface p = board.getPieceAt(checkPawn);
//                if (p != null && p.getColor() != color && p instanceof Pawn) {
//                    return true;
//                }
//            }
//        }

        return false;
    }

    private int[][] getOffsetsForDirection(ChessMoveType moveType) {
        switch (moveType) {
            case Vertical:
                return new int[][] { {1, -1}, {0, 0} };  // dx, dy
            case Horizontal:
                return new int[][] { {0, 0}, {1, -1} };
            case Diagonal:
                return new int[][] { {1, 1, -1, -1}, {1, -1, 1, -1} };
            default:
                return new int[][] { {}, {} };
        }
    }
    public String getSymbol(){
        if(color==Color.White)
            return "k";
        else
            return "K";
    }


}
