package com.ddalggak.finalproject.domain.message.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import com.ddalggak.finalproject.domain.message.dto.ChatMessageDto;
import com.ddalggak.finalproject.domain.message.dto.ChatMessageDto;
import com.ddalggak.finalproject.domain.message.entity.ChatMessage;
import com.ddalggak.finalproject.domain.message.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	private final ChatService chatService;
	@Autowired
	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}


	/**
	 * 채팅방 입장
	 */
	@PostMapping("/rooms/{roomId}/enter")
	public ResponseEntity<Void> enterChatRoom(@PathVariable Long roomId, @RequestBody String email) {
		chatService.enterChatRoom(roomId, email);
		return ResponseEntity.ok().build();
	}

	/**
	 * 채팅방 퇴장
	 */
	// @PostMapping("/rooms/{roomId}/leave")
	// public ResponseEntity<Void> leaveChatRoom(@PathVariable Long roomId, @RequestBody String email) {
	// 	chatService.leaveChatRoom(roomId, email);
	// 	return ResponseEntity.ok().build();
	// }

	/**
	 * 메시지 전송
	 */
	// @PostMapping("/messages")
	// public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody ChatMessageDto chatMessageDto) {
	// 	chatService.sendMessage(chatMessageDto);
	// 	return ResponseEntity.ok().body(chatMessageDto);
	// }

	/**
	 * 채팅방의 모든 메시지 조회
	 */
	// @GetMapping("/rooms/{roomId}/messages")
	// public ResponseEntity<List<ChatMessageDto>> getChatMessages(@PathVariable Long roomId){
	// 	List<ChatMessageDto> messageDtoList = chatService.getChatMessages(roomId);
	// 	return ResponseEntity.ok().body(messageDtoList);
	// }
	//
	// @MessageMapping("/chat/{roomId}")
	// @SendTo("/topic/chat/{roomId}")
	// public ChatMessageDto sendMessage(@DestinationVariable Long roomId, ChatMessageDto messageDto) {
	// 	chatService.sendMessage(messageDto);
	// 	return messageDto;
	// }
}

