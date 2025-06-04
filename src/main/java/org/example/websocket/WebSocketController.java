package org.example.websocket;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;



    public void sendChessboard(Long idPlayer,String fen) throws Exception {
        simpMessagingTemplate.convertAndSend("/board/" + idPlayer, fen);
    }
    public void sendForfeit(Long idPlayer, Map<String,Object> jsonBody){
        System.out.println("Forfeit body");
        System.out.println(jsonBody);
        System.out.println(idPlayer);
        simpMessagingTemplate.convertAndSend("/game-over/" + idPlayer, jsonBody);
    }
}