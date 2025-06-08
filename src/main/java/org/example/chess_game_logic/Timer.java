//package org.example.chess_game_logic;
//
//
//
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class Timer implements Runnable {
//    private volatile  int availableTimeInSec;
//    //    private AtomicBoolean stillCounting=new AtomicBoolean(false);
//    private AtomicBoolean encounteredError=new AtomicBoolean(true);
//    public Timer(int timeInMinutes,){
//        this.availableTimeInSec=60*timeInMinutes;
//
//    }
//
//
//    public void run(){
////        stillCounting.set(true);
//        Object obj=new Object();
//        try {
//            while (availableTimeInSec > 0 ) {
//                Thread.sleep(1000);
//                availableTimeInSec -= 1;
//            }
//        }
//        catch (InterruptedException exc){
//            encounteredError.set(true);
//            System.out.println("Problem enc: "+exc.getMessage());
//        }
//    }
//    //    public void stopTimer(){
////        stillCounting.set(false);
////    }
//    @Override
//    public String toString(){
//        return "Time left: "+availableTimeInSec;
//    }
//}
