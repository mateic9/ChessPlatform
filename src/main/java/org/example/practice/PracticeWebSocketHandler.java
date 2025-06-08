//package org.example.practice;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class PracticeWebSocketHandler extends TextWebSocketHandler {
//
//    private static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        long userId = extractUserId(session);
//        sessions.put(userId, session);
//    }
//
//    public static void sendFenToUser(long userId, String fen, boolean isPlayerTurn) {
//        WebSocketSession session = sessions.get(userId);
//        if (session != null && session.isOpen()) {
//            try {
//                Map<String, Object> payload = Map.of(
//                        "fen", fen,
//                        "isPlayerTurn", isPlayerTurn
//                );
//                String json = new ObjectMapper().writeValueAsString(payload);
//                session.sendMessage(new TextMessage(json));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private int extractUserId(WebSocketSession session) {
//        String query = session.getUri().getQuery(); // e.g., "userId=123"
//        for (String param : query.split("&")) {
//            String[] pair = param.split("=");
//            if (pair[0].equals("userId")) {
//                return Integer.parseInt(pair[1]);
//            }
//        }
//        throw new IllegalArgumentException("Missing userId");
//    }
//
//}
