package org.example.chess_game_logic;

import lombok.Getter;
import org.example.chess_game_logic.chess_pieces.*;
import org.example.exceptions.MovePieceException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class Board {

    private final PieceInterface[][] board = new PieceInterface[8][8];
    @Getter
    private Map<Color, Position> kingPozMap=new HashMap<>();
    @Getter
    private String enPassantTarget = "-";

    @Getter
    private int halfmoveClock = 0;

    @Getter
    private int fullmoveNumber = 1;
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
        if((piece) instanceof Pawn)
            ((Pawn)piece).setMoved(true);


    }
    public boolean isOnBoard(Position p){
        return (0<=p.getX()&&p.getX()<=7&&0<=p.getY()&&p.getY()<=7);
    }
    private void initializeBoard() {
        Position kingWhitePos=new Position(7,4);
        Position kingBlackPos=new Position(0,4);
        kingPozMap.put(Color.White,kingWhitePos);
        kingPozMap.put(Color.Black,kingBlackPos);

        this.setPieceAt(new Position(7,0),new Rook(Color.White));
        this.setPieceAt(new Position(7,1),new Knight(Color.White));
        this.setPieceAt(new Position(7,2),new Bishop(Color.White));
        this.setPieceAt(new Position(7,3),new Queen(Color.White));
        this.setPieceAt(new Position(7,4),new King(Color.White));
        this.setPieceAt(new Position(7,5),new Bishop(Color.White));
        this.setPieceAt(new Position(7,6),new Knight(Color.White));
        this.setPieceAt(new Position(7,7),new Rook(Color.White));

        ///white pawns init
        this.setPieceAt(new Position(6,0),new Pawn(Color.White));
        this.setPieceAt(new Position(6,1),new Pawn(Color.White));
        this.setPieceAt(new Position(6,2),new Pawn(Color.White));
        this.setPieceAt(new Position(6,3),new Pawn(Color.White));
        this.setPieceAt(new Position(6,4),new Pawn(Color.White));
        this.setPieceAt(new Position(6,5),new Pawn(Color.White));
        this.setPieceAt(new Position(6,6),new Pawn(Color.White));
        this.setPieceAt(new Position(6,7),new Pawn(Color.White));


        ///black pieces
        this.setPieceAt(new Position(0,0),new Rook(Color.Black));
        this.setPieceAt(new Position(0,1),new Knight(Color.Black));
        this.setPieceAt(new Position(0,2),new Bishop(Color.Black));
        this.setPieceAt(new Position(0,3),new Queen(Color.Black));
        this.setPieceAt(new Position(0,4),new King(Color.Black));
        this.setPieceAt(new Position(0,5),new Bishop(Color.Black));
        this.setPieceAt(new Position(0,6),new Knight(Color.Black));
        this.setPieceAt(new Position(0,7),new Rook(Color.Black));


        ///black pawns

        this.setPieceAt(new Position(1,0),new Pawn(Color.Black));
        this.setPieceAt(new Position(1,1),new Pawn(Color.Black));
        this.setPieceAt(new Position(1,2),new Pawn(Color.Black));
        this.setPieceAt(new Position(1,3),new Pawn(Color.Black));
        this.setPieceAt(new Position(1,4),new Pawn(Color.Black));
        this.setPieceAt(new Position(1,5),new Pawn(Color.Black));
        this.setPieceAt(new Position(1,6),new Pawn(Color.Black));
        this.setPieceAt(new Position(1,7),new Pawn(Color.Black));
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
    public void promote(Position promotePos,String playerChoice){
         Color color =this.getPieceAt(promotePos).getColor();
         PieceInterface promotedPiece = null;
        switch (playerChoice){
            case "Bishop": promotedPiece=new Bishop(color);break;
            case "Knight": promotedPiece=new Knight(color);break;
            case "Rook": promotedPiece=new  Rook(color);break;
            case "Queen": promotedPiece=new Queen(color);break;

        }
        this.setPieceAt(promotePos,promotedPiece);
    }
    List<Position> getFutureKingPos(Color kingColor){
        Position kingPos=kingPozMap.get(kingColor);
        List<Position> result=new ArrayList<Position>();
        int curX= kingPos.getX();
        int curY= kingPos.getY();
        for(int i=-1;i<=1;i++)
            for(int j=-1;j<=1;j++){
                Position p=new Position(curX+i,curY+j);
                if(this.isOnBoard(p)&&(i!=0&&j!=0))
                    result.add(p);
            }
        for(Position p:result)
            System.out.println(p);
        return result;
    }
    public Map<Position,PieceInterface> getPiecesLeftByColor(Color color){
        Map<Position,PieceInterface> piecesLeft=new HashMap<Position,PieceInterface>();
        for(int i=0;i<=7;i++)
            for(int j=0;j<=7;j++)
                if(board[i][j]!=null && board[i][j].getColor()==color&& !(board[i][j] instanceof King)){
                    piecesLeft.put(new Position(i,j),board[i][j]);
                }
          return piecesLeft;
    }
    public String getPiecePositionFen(){
        StringBuilder fenReprez= new StringBuilder();
        StringBuilder row;
        int i,j,emptySpaces;
        for( i=0;i<=7;i++){
            row = new StringBuilder();
            emptySpaces=0;
            for(j=0;j<=7;j++)
              if(board[i][j]==null)
                  emptySpaces+=1;
              else{
                  if(emptySpaces!=0)
                    row.append(emptySpaces);
                  emptySpaces=0;
                  row.append(board[i][j].getSymbol());
              }
              if(emptySpaces!=0)
                  row.append(emptySpaces);
              if(i!=7)
                row.append("/");
            fenReprez.append(row);
        }

        return fenReprez.toString();
    }

//    public String getRealFen(Color colorToMove) {
//        StringBuilder fen = new StringBuilder();
//
//        // 1. Piece positions
//        fen.append(getPiecePositionFen()).append(" ");
//
//        // 2. Active color
//        fen.append(colorToMove == Color.White ? "w" : "b").append(" ");
//
//        // 3. Castling rights
//        StringBuilder castling = new StringBuilder();
//
//        // White king and rooks
//        Position whiteKingPos = kingPozMap.get(Color.White);
//        PieceInterface whiteKing = getPieceAt(whiteKingPos);
//        if (whiteKing instanceof King && !((King) whiteKing).isMoved()) {
//            PieceInterface whiteKingsideRook = getPieceAt(new Position(0, 7));
//            if (whiteKingsideRook instanceof Rook && !((Rook) whiteKingsideRook).isMoved()) {
//                castling.append("K");
//            }
//            PieceInterface whiteQueensideRook = getPieceAt(new Position(0, 0));
//            if (whiteQueensideRook instanceof Rook && !((Rook) whiteQueensideRook).isMoved()) {
//                castling.append("Q");
//            }
//        }
//
//        // Black king and rooks
//        Position blackKingPos = kingPozMap.get(Color.Black);
//        PieceInterface blackKing = getPieceAt(blackKingPos);
//        if (blackKing instanceof King && !((King) blackKing).isMoved()) {
//            PieceInterface blackKingsideRook = getPieceAt(new Position(7, 7));
//            if (blackKingsideRook instanceof Rook && !((Rook) blackKingsideRook).isMoved()) {
//                castling.append("k");
//            }
//            PieceInterface blackQueensideRook = getPieceAt(new Position(7, 0));
//            if (blackQueensideRook instanceof Rook && !((Rook) blackQueensideRook).isMoved()) {
//                castling.append("q");
//            }
//        }
//
//        fen.append(castling.length() > 0 ? castling : "-").append(" ");
//
//
//        fen.append("- ");
//
//
//        fen.append("0 ");
//
//
//        fen.append("1");
//
//        return fen.toString();
//    }
public String getRealFen(Color colorToMove) {
    StringBuilder fen = new StringBuilder();

    // 1. Piece positions
    fen.append(getPiecePositionFen()).append(" ");

    // 2. Active color
    fen.append(colorToMove == Color.White ? "w" : "b").append(" ");

    // 3. Castling rights
    StringBuilder castling = new StringBuilder();

    Position whiteKingPos = kingPozMap.get(Color.White);
    PieceInterface whiteKing = getPieceAt(whiteKingPos);
    if (whiteKing instanceof King && !((King) whiteKing).isMoved()) {
        PieceInterface whiteKingsideRook = getPieceAt(new Position(0, 7));
        if (whiteKingsideRook instanceof Rook && !((Rook) whiteKingsideRook).isMoved()) castling.append("K");
        PieceInterface whiteQueensideRook = getPieceAt(new Position(0, 0));
        if (whiteQueensideRook instanceof Rook && !((Rook) whiteQueensideRook).isMoved()) castling.append("Q");
    }

    Position blackKingPos = kingPozMap.get(Color.Black);
    PieceInterface blackKing = getPieceAt(blackKingPos);
    if (blackKing instanceof King && !((King) blackKing).isMoved()) {
        PieceInterface blackKingsideRook = getPieceAt(new Position(7, 7));
        if (blackKingsideRook instanceof Rook && !((Rook) blackKingsideRook).isMoved()) castling.append("k");
        PieceInterface blackQueensideRook = getPieceAt(new Position(7, 0));
        if (blackQueensideRook instanceof Rook && !((Rook) blackQueensideRook).isMoved()) castling.append("q");
    }

    fen.append(castling.length() > 0 ? castling.toString() : "-").append(" ");

    // 4. En passant
    fen.append(enPassantTarget != null ? enPassantTarget : "-").append(" ");

    // 5. Halfmove clock
    fen.append(halfmoveClock).append(" ");

    // 6. Fullmove number
    fen.append(fullmoveNumber);

    return fen.toString();
}


    public void registerActualMove(Position from, Position to, Color color, boolean isCapture) {
        PieceInterface piece = getPieceAt(to); // After movePiece() has been called

        // Reset en passant by default
        enPassantTarget = "-";

        // Update en passant if pawn moved two squares
        if (piece instanceof Pawn) {
            int dx = Math.abs(from.getX() - to.getX());
            if (dx == 2) {
                int middleX = (from.getX() + to.getX()) / 2;
                char file = (char) ('a' + from.getY());
                int rank = 8 - middleX;
                enPassantTarget = file + String.valueOf(rank);
            }
            halfmoveClock = 0;
        } else if (isCapture) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }

        if (color == Color.Black) {
            fullmoveNumber++;
        }
    }






}
