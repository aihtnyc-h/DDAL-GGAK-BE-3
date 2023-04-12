package com.ddalggak.finalproject.global.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.ddalggak.finalproject.global.config.WebSocketConfig;

public class SocketHandler extends TextWebSocketHandler {
	private List<WebSocketSession> sessions = new ArrayList<>();
	// WebSocketSession 저장
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
	}
	// WebSocketSession 삭제
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
	}

	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		session.sendMessage(new TextMessage("알림 메세지"));
	}
}
