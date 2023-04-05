package com.ddalggak.finalproject.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {
	@Override
	public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
		messageBrokerRegistry.enableSimpleBroker("/sub");
		messageBrokerRegistry.setApplicationDestinationPrefixes("/pub");
	}
	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
		stompEndpointRegistry.addEndpoint("/stomp/chat")
			.setAllowedOrigins("http://localhost:8080")
			.setAllowedOrigins("*") // "chrome-extension: url"
			.withSockJS();
	}
}
