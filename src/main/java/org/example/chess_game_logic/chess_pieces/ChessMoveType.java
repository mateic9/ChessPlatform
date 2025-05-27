package org.example.chess_game_logic.chess_pieces;

import java.util.ArrayList;
import java.util.List;

public enum ChessMoveType {
    WrongMove{
        @Override
        public String toString(){
            return "Wrong move";
        }
    },

    Vertical {
        @Override
        public String toString() {
            return "Vertical move";
        }

        @Override
        public int getOffsetX(Position c, Position d) {
            return c.getX() < d.getX() ? 1 : -1;
        }

        @Override
        public int getOffsetY(Position c, Position d) {
            return 0;
        }

    },

    Horizontal {
        @Override
        public String toString() {
            return "Horizontal move";
        }

        @Override
        public int getOffsetX(Position c, Position d) {
            return 0;
        }

        @Override
        public int getOffsetY(Position c, Position d) {
            return c.getY() < d.getY() ? 1 : -1;
        }
    },

    Diagonal {
        @Override
        public String toString() {
            return "Diagonal move";
        }

        @Override
        public int getOffsetX(Position c, Position d) {
            return c.getX() < d.getX() ? 1 : -1;
        }

        @Override
        public int getOffsetY(Position c, Position d) {
            return c.getY() < d.getY() ? 1 : -1;
        }
    },



    KnightMove {

        @Override
        public String toString(){
            return "Knight move";
        }
    };


    // Default method for enums that don't override it

    public String toString(){
        throw new UnsupportedOperationException("Offsets not defined for " + this.name());
    }
    public int getOffsetX(Position curPosition,Position destPosition){
        throw new UnsupportedOperationException("Offsets not defined for " + this.name());
    }
    public int getOffsetY(Position curPosition,Position destPosition){
        throw new UnsupportedOperationException("Offsets not defined for " + this.name());
    }
    public List<Position> getPath(Position start,Position end){
        List<Position> response=new ArrayList<Position>();
        int offsetX=this.getOffsetX(start,end);
        int offsetY=this.getOffsetY(start,end);
        int curX= start.getX()+offsetX;
        int curY=start.getY()+offsetY;
        System.out.println("Start: "+start);
        System.out.println("End: "+end);
        while(curX!= end.getX()|| curY!=end.getY()){
            Position p= new Position(curX,curY);
            response.add(p);
            System.out.println(p);
            curX+=offsetX;
            curY+=offsetY;
        }
        return response;
    }

}
