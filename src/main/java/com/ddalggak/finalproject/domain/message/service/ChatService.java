package com.ddalggak.finalproject.domain.message.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.ddalggak.finalproject.domain.message.dto.ChatMessageDto;
import com.ddalggak.finalproject.domain.message.entity.ChatMessage;
import com.ddalggak.finalproject.domain.message.entity.ChatRoom;
import com.ddalggak.finalproject.domain.message.repository.ChatMessageRepository;
import com.ddalggak.finalproject.domain.message.repository.ChatRoomRepository;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService extends ChatServiceImpl {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final UserRepository userRepository;

	/**
	 * 채팅방 입장
	 */
	// email로 사람을 조회할 것인지. 아니면 이메일과 닉네임 둘다 조회할 것인지 => 둘다 만들장~ -> 귀찬ㅠ
	// public void enterChatRoom(Long roomId, String email) {
	// 	ChatRoom room; // Optional이 비어있을 때 새 ChatRoom 객체를 생성하여 반환
	// 	room = (ChatRoom)chatRoomRepository.findById(roomId).orElse(new ChatRoom());
	// 	room.getUserList().add(email);
	// 	chatRoomRepository.save(room);
	// }
	public ResponseEntity<?> enterChatRoom(Long roomId, String email) {
		User user = validateUserByEmail(email);
		ChatRoom room = getChatRoomOrThrow(roomId);
		room.addUser(user);
		chatRoomRepository.save(room);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_SEND);
	}

	private ChatRoom getChatRoomOrThrow(Long roomId) {
		return chatRoomRepository.findById(roomId)
			.orElseThrow(() -> new CustomException(CHATROOM_NOT_FOUND));
	}
	private User validateUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(UNAUTHENTICATED_USER));
	}

	/**
	 * 채팅방 퇴장
	 */
	// public void leaveChatRoom(Long roomId, String email) {
	// 	ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
	// 	room.getUserList().remove(email);
	// 	chatRoomRepository.save(room);
	// }
	public void leaveChatRoom(Long roomId, String email) {
		User user = validateUserByEmail(email);
		ChatRoom room = getChatRoomOrThrow(roomId);
		if (!room.getUserList().contains(user)) {
			// User is not in the chat room
			return;
		}
		room.removeUser(user);
		chatRoomRepository.save(room);
	}

	/**
	 * 메시지 전송
	 */
	public void sendMessage(ChatMessageDto messageDto) {
		ChatRoom room = chatRoomRepository.findById(messageDto.getRoomId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다.")); // 오류코드 만들어야한다네 언제할것인가?

		User sender = userRepository.findById(messageDto.getUser().getUserId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		ChatMessage message = new ChatMessage();
		messageDto.setContent(messageDto.getContent());
		messageDto.setRoomId(room.getChatRoomId());
		messageDto.setUser(sender);
		messageDto.setCreatedAt(LocalDateTime.now());
			// room.getChatRoomId(), sender, LocalDateTime.now());
		ChatMessage savedMessage = chatMessageRepository.save(message);
		// messageDto.setChatMessageId(savedMessag);
		messageDto.setChatMessageId(savedMessage.getChatMessageId());
		messageDto.setCreatedAt(savedMessage.getCreatedAt());

		messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getRoomId(), messageDto);
	}

	/**
	 * 채팅방의 모든 메시지 조회
	 */
	public List<ChatMessageDto> getChatMessages(Long roomId) {
		List<ChatMessage> messages = chatMessageRepository.findAllByRoomIdOrderByCreatedAtAsc(roomId);
		return messages.stream()
			.map(ChatMessageDto::from)
			.collect(Collectors.toList());
	}
	//페이지 단위로 채팅방 조회
	public List<ChatMessageDto> getChatMessages(Long roomId, int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").ascending());
		Page<ChatMessage> messagePage = (Page<ChatMessage>)chatMessageRepository.findAllByRoomIdOrderByCreatedAtAsc(roomId);
		List<ChatMessageDto> messageDtoList = messagePage.getContent()
			.stream()
			.map(ChatMessageDto::from)
			.collect(Collectors.toList());
		return messageDtoList;
	}
}


