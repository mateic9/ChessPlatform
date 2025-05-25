package org.example.chess_game_logic.chess_pieces;

import lombok.Getter;
import org.example.chess_game_logic.MovePieceException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class Board {

    private final PieceInterface [][] board = new PieceInterface[8][8];
    @Getter
    private Map<Color,Position> kingPozMap=new HashMap<>();
    public Board() {
        initializeBoard(); // optional: set up initial chess positions
    }

    public PieceInterface getPieceAt(Position pos) {
        return board[pos.getX()][pos.getY()];
    }

    public void setPieceAt(Position pos, PieceInterface piece) {
        board[pos.getX()][pos.getY()] = piece;
    }

    public void movePiece(Position from, Position to) {
        PieceInterface piece = getPieceAt(from);
        setPieceAt(to, piece);
        setPieceAt(from, null);
        if(piece instanceof King) {
            kingPozMap.put(piece.getColor(),to);
            ((King) piece).setMoved(true);
        }
        if((piece) instanceof Rook)
             ((Rook) piece).setMoved(true);

    }
    public boolean isOnBoard(Position p){
        return (0<=p.getX()&&p.getX()<=7&&0<=p.getY()&&p.getY()<=7);
    }
    private void initializeBoard() {
        Position kingWhitePos=new Position(0,4);
        Position kingBlackPos=new Position(7,4);
       this.setPieceAt(kingWhitePos,new King(Color.White));
        this.setPieceAt(kingBlackPos,new King(Color.Black));
        this.setPieceAt(new Position(0,3),new Queen(Color.White));
        this.setPieceAt(new Position(7,3),new Queen(Color.Black));
        this.setPieceAt(new  Position(0,7),new Rook(Color.White));
        this.setPieceAt(new Position(0,5),new Bishop(Color.White));
        this.setPieceAt(new Position(5,6),new Knight(Color.White));
        this.setPieceAt(new Position(6,4),new Pawn(Color.White));
        kingPozMap.put(Color.White,kingWhitePos);
        kingPozMap.put(Color.Black,kingBlackPos);
    }
    public void printBoard(){
        int i,j;
        for(i=0;i<=7;i++) {
            for (j = 0; j <= 7; j++) {
                PieceInterface piece = this.getPieceAt(new Position(i, j));
                if (piece == null)
                    System.out.print("-");
                else
                    System.out.print(piece.getSymbol());
            }
            System.out.println();
        }

    }
    public void makeCastle(Position kingPoz,Position rookPoz){
        int row=kingPoz.getX();
        int colRook=rookPoz.getY();
        if(colRook!=0 && colRook!=7)
            throw new MovePieceException("Castle gone wrong!");
        if(colRook==0){
             movePiece(kingPoz,new Position(row,2));
             movePiece(rookPoz,new Position(row,3));
        }
        else{
            movePiece(kingPoz,new Position(row,6));
            movePiece(rookPoz,new Position(row,5));
        }
    }
    public void undoCastle(Position kingPoz,Position rookPoz){
        int row=kingPoz.getX();
        int colRook=rookPoz.getY();
        if(colRook!=0 && colRook!=7)
            throw new MovePieceException("Castle gone wrong!");
        if(colRook==0){
            movePiece(new Position(row,2),kingPoz);
            movePiece(new Position(row,3),rookPoz);
        }
        else{
            movePiece(new Position(row,6),kingPoz);
            movePiece(new Position(row,5),rookPoz);
        }
        PieceInterface king=this.getPieceAt(kingPoz);
        PieceInterface rook=this.getPieceAt(rookPoz);

        if(!(king instanceof King) ||!(rook instanceof Rook))
            throw new MovePieceException("UNcastle gone wrong!");
        ((King) king).setMoved(false);
        ((Rook) rook).setMoved(false);

    }
}
