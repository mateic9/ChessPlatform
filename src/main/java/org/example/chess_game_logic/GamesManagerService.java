package org.example.chess_game_logic;
import org.example.chess_game_logic.chess_pieces.MoveValidator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class GamesManagerService {
    //    private final Queue<Long> waitingPlayers = new ConcurrentLinkedQueue<>();
    CyclicBarrier barrier1 = new CyclicBarrier(2, () -> {
        System.out.println("Barrier 1 reached!");
    });
    private volatile boolean isGameCreated;
    private final Map<Long, ChessGame> activeGames = new ConcurrentHashMap<>();
    volatile Long idFirstPlayer= (long) -1;
    volatile Long idSecondPlayer= (long) -1;
    private static ReentrantLock smallLock= new ReentrantLock();
    private volatile ChessGame gameToBeCreated=null;

    void processMove(MovePieceRequest request) throws MovePieceException{
        ChessGame currentGame=this.findGame(request.getIdPlayer());
        if(currentGame==null)
            throw new MovePieceException("Nu exista acest joc");
        synchronized (currentGame){

            currentGame.processMove(request);


        }

    }
    ChessGame processJoinRequest(JoinGameRequest request){
        try {
           smallLock.lock();
           while(idFirstPlayer!=-1 && idSecondPlayer!=-1){

           }
           if(idFirstPlayer==-1)
               idFirstPlayer= request.getIdPlayer();
           else
               idSecondPlayer= request.getIdPlayer();
           smallLock.unlock();

           barrier1.await();
           isGameCreated=false;

           if(request.getIdPlayer().equals(idFirstPlayer)){
                  Long idGame=(long) 5;
                  gameToBeCreated=new ChessGame(idGame,idFirstPlayer,idSecondPlayer,new MoveValidator());
                  isGameCreated=true;
           }

           while(!isGameCreated){

           }
           System.out.println("A game with id: "+gameToBeCreated.getIdGame()+" was created");
           activeGames.put(request.getIdPlayer(),gameToBeCreated);
           if(idFirstPlayer.equals(request.getIdPlayer()))
               idFirstPlayer=(long) -1;
           if(idSecondPlayer.equals((request.getIdPlayer())))
               idSecondPlayer=(long)-1;

        }


        catch (InterruptedException | BrokenBarrierException e ){
            System.out.println("Probleme la bariera");
            System.out.println(e.getMessage());
        }
        return activeGames.get(request.getIdPlayer());

    }
    ChessGame findGame(Long idPlayer){
         return activeGames.get(idPlayer);
    }
}

//package org.example.chess_game_logic;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.concurrent.BrokenBarrierException;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.CyclicBarrier;
//import java.util.concurrent.locks.ReentrantLock;
//
//@Service
//public class GamesManagerService {
////    private final Queue<Long> waitingPlayers = new ConcurrentLinkedQueue<>();
//    CyclicBarrier barrier = new CyclicBarrier(2, () -> {
//        System.out.println("All threads reached the barrier!");
//    });
//    private final Map<Long, ChessGame> activeGames = new ConcurrentHashMap<>();
//     volatile Long idFirstPlayer= (long) -1;
//     volatile Long idSecondPlayer= (long) -1;
//    private static ReentrantLock smallLock= new ReentrantLock();
//    private static  ReentrantLock gameCreationLock=new ReentrantLock();
//    private volatile ChessGame currentGame=null;
//    void processMove(MovePieceRequest request) throws MovePieceException{
//        ChessGame currentGame=this.findGame(request.getIdPlayer());
//        if(currentGame==null)
//            throw new MovePieceException("Nu exista acest joc");
//        synchronized (currentGame){
//
//            currentGame.processMove(request);
//
//
//        }
//
//    }
//    void processJoinRequest(JoinGameRequest request){
//      try {
//          barrier.await();
//
//         smallLock.lock();
//         while(idFirstPlayer !=-1 && idSecondPlayer!=-1){
//
//         }
//          if (idFirstPlayer == -1)
//              idFirstPlayer = request.getIdPlayer();
//          else
//              idSecondPlayer= request.getIdPlayer();
//
//          smallLock.unlock();
//         gameCreationLock.lock();
//         if(currentGame==null)
//             currentGame=new ChessGame((long) 3,idFirstPlayer,idSecondPlayer);
//         activeGames.put(request.getIdPlayer(),currentGame);
//         gameCreationLock.unlock();
//         currentGame=null;
//         idFirstPlayer=(long) -1;
//         idSecondPlayer=(long) -1;
//      }
//      catch (InterruptedException | BrokenBarrierException e ){
//          System.out.println("Probleme la bariera");
//          System.out.println(e.getMessage());
//      }
//      finally {
//
//      }
//    }
//    ChessGame findGame(Long idPlayer){
//
//    }
//}
//
