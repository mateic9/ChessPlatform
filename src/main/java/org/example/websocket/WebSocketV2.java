package org.example.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketV2 implements WebSocketMessageBrokerConfigurer {

   @Override

   public void registerStompEndpoints(StompEndpointRegistry registry) {
       registry.addEndpoint("/ws")
               .setAllowedOriginPatterns("*") // ← allow cross-origin connections
               .withSockJS();
   }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/board", "/game-over","/time","/practice");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
