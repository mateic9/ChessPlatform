//package org.example.websocker;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.io.IOException;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class SocketRegistry {
//    // userId  ->  session
//    private final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
//    private final ObjectMapper mapper = new ObjectMapper();
//    public void add(Long userId, WebSocketSession session) {
//        sessions.put(userId, session);
//    }
//    public void remove(Long userId) {
//        sessions.remove(userId);
//    }
//
//    public void send(Long playerId, Object payload) throws IOException {
//        WebSocketSession session = sessions.get(playerId);
//        if (session != null && session.isOpen()) {
//            String json = mapper.writeValueAsString(payload);
//            session.sendMessage(new TextMessage(json));
//        }
//    }
////    public void send(Long userId, Object payload) throws IOException {
////        WebSocketSession session = sessions.get(userId);
////        if (session != null && session.isOpen()) {
////            session.sendMessage(
////                    new TextMessage(new ObjectMapper().writeValueAsString(payload))
////            );
////        }
////    }
//}