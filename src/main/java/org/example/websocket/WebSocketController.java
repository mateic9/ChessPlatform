package org.example.websocket;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Controller;
@Controller
public class WebSocketController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;



    public void send(Long idPlayer,String fen) throws Exception {
        simpMessagingTemplate.convertAndSend("/board/" + idPlayer, fen);
    }
}