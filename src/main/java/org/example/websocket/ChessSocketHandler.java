//package org.example.websocker;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.Optional;
//
//@Component
//public class ChessSocketHandler extends TextWebSocketHandler {
//
//    private final SocketRegistry registry;
//
//    public ChessSocketHandler(SocketRegistry registry) {
//        this.registry = registry;
//    }
////
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        //  ws://host:8080/ws/chess?playerId=123
//        Long userId = Long.valueOf(
//                Optional.ofNullable((String) session.getAttributes().get("playerId"))
//                        .orElseThrow(() -> new IllegalStateException("playerId missing"))
//        );
//        registry.add(userId, session);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        Object id = session.getAttributes().get("playerId");
//        if (id != null) registry.remove(Long.valueOf((String) id));
//    }
//}
