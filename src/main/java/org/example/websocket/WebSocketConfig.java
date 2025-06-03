//package org.example.websocker;
//
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.util.Map;
//import org.springframework.context.annotation.Configuration;
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    private final ChessSocketHandler handler;
//
//    public WebSocketConfig(ChessSocketHandler handler) {
//        this.handler = handler;
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry reg) {
//        reg.addHandler(handler, "/ws/chess")
//                .addInterceptors(new HttpSessionHandshakeInterceptor() {
//                    @Override
//                    public boolean beforeHandshake(ServerHttpRequest req,
//                                                   ServerHttpResponse res,
//                                                   WebSocketHandler wsHandler,
//                                                   Map<String, Object> attrs) {
//                        URI uri = req.getURI();
//                        MultiValueMap<String,String> q = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
//                        attrs.put("playerId", q.getFirst("playerId"));   // <- for  handler
//                        return true;
//                    }
//                })
//                .setAllowedOrigins("*");
//    }
//}
