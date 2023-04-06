package com.ddalggak.finalproject.global.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatHandler extends TextWebSocketHandler {
	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String roomId = getRoomId(session);
		sessions.put(roomId, session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String roomId = getRoomId(session);
		TextMessage msg = new TextMessage("[" + roomId + "] " + message.getPayload());
		broadcast(roomId, msg);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String roomId = getRoomId(session);
		sessions.remove(roomId);
	}

	private String getRoomId(WebSocketSession session) {
		return session.getUri().getPath().split("/")[2];
	}

	private void broadcast(String roomId, TextMessage message) throws IOException {
		for (WebSocketSession session : sessions.values()) {
			if (session.isOpen() && roomId.equals(getRoomId(session))) {
				session.sendMessage(message);
			}
		}
	}
}