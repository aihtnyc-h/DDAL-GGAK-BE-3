package com.ddalggak.finalproject.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.ddalggak.finalproject.global.handler.SocketHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
// @EnableWebSocketMessageBroker
// @RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {//WebSocketMessageBrokerConfigurer {
	// private final StompHandler stompHandler;	// jwt 토큰 인증 핸들러
	// @Override
	// public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
	// 	stompEndpointRegistry.addEndpoint("/we-stomp").withSockJS();
	// }
	// @Override
	// public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
	// 	messageBrokerRegistry.enableSimpleBroker("/sub");
	// 	messageBrokerRegistry.setApplicationDestinationPrefixes("/pub");
	// }
	// @Override
	// public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
	// 	channelRegistration.interceptors(stompHandler); // 핸들러 등록
	// }

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new SocketHandler(), "/socket").setAllowedOrigins("*");
	}
}
