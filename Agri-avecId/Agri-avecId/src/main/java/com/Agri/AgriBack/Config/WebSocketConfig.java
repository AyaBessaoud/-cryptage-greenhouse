package com.Agri.AgriBack.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  //enables WebSocket message handling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //message broker to carry the messages back to the client on destinations prefixed with /topic
        config.enableSimpleBroker("/topic"); // les clients s’abonnent à /topic/...
        config.setApplicationDestinationPrefixes("/app"); // les clients envoient à /app/...
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notif") //registers the /notif endpoint for websocket connections
                .setAllowedOrigins("http://localhost:4200") // autoriser Angular
                .withSockJS(); // compatibilité navigateur
    }
}
