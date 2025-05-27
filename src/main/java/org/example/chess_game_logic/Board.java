package org.example.chess_game_logic;

import lombok.Getter;
import org.example.chess_game_logic.chess_pieces.*;
import org.example.chess_game_logic.requests.MovePieceRequest;
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
        Position kingWhitePos=new Position(5,1);
        Position kingBlackPos=new Position(7,0);
       this.setPieceAt(kingWhitePos,new King(Color.White));
       this.setPieceAt(kingBlackPos,new King(Color.Black));
       this.setPieceAt(new Position(6,5),new Queen(Color.White));
       this.setPieceAt(new Position(6,6),new Rook(Color.Black));
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


}
