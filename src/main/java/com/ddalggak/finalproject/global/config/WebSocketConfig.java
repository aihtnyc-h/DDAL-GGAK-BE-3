package com.ddalggak.finalproject.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.ddalggak.finalproject.global.handler.ChatHandler;
import com.ddalggak.finalproject.global.handler.SocketHandler;
import com.ddalggak.finalproject.global.handler.StompHandler;

import lombok.RequiredArgsConstructor;

// @Configuration
// @EnableWebSocket
// @EnableWebSocketMessageBroker
// @RequiredArgsConstructor
// public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
// 	private StompHandler stompHandler;	// jwt 토큰 인증 핸들러
// 	// @Override
// 	// public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
// 	// 	stompEndpointRegistry.addEndpoint("/we-stomp").withSockJS();
// 	// }
// 	@Override
// 	public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
// 		messageBrokerRegistry.enableSimpleBroker("/sub");
// 		messageBrokerRegistry.setApplicationDestinationPrefixes("/pub");
// 	}
// 	// @Override
// 	// public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
// 	// 	channelRegistration.interceptors(stompHandler); // 핸들러 등록
// 	// }
// 	@Override
// 	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
// 		stompEndpointRegistry.addEndpoint("/stomp/chat")
// 			.setAllowedOriginPatterns("*")
// 			.withSockJS();
// 	}
//
// 	// @Override
// 	// public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
// 	// 	registry.addHandler(new SocketHandler(), "/socket").setAllowedOrigins("*");
// 	// }
// }
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(chatHandler(), "/chat/{roomId}")
			.setAllowedOrigins("*")
			.withSockJS();
	}

	@Bean
	public WebSocketHandler chatHandler() {
		return new ChatHandler();
	}
}
