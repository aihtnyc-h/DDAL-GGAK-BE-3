// package com.ddalggak.finalproject.domain.message.dto;
// //
// // import java.time.LocalDateTime;
// //
// // import com.ddalggak.finalproject.domain.message.entity.ChatMessage;
// // import com.ddalggak.finalproject.domain.user.entity.User;
// //
// // import lombok.Builder;
// // import lombok.Getter;
// // import lombok.Setter;
// //
// // @Getter
// // @Setter
// // public class ChatMessageDto {
// //
// // 	public Long chatMessageId;
// //
// // 	public String content;
// //
// // 	public Long roomId;
// //
// // 	public User user;
// // 	public LocalDateTime createdAt;
// //
// // 	public ChatMessageDto(Long chatMessageId, String content, Long roomId, User user, LocalDateTime createdAt) {
// // 		this.chatMessageId = chatMessageId;
// // 		this.content = content;
// // 		this.roomId = roomId;
// // 		this.user = user;
// // 		this.createdAt = createdAt;
// // 	}
// // 	@Builder
// // 	public ChatMessageDto (ChatMessage message) {
// // 		chatMessageId = message.getChatMessageId();
// // 		content = message.getContent();
// // 		roomId = message.getRoomId();
// // 		user = message.getUser().getUserId();
// // 		createdAt = message.getCreatedAt();
// //
// // 		// messageDto.getChatMessageId();//.setChatMessageId(message.getId());
// // 		// messageDto.getContent();  //messageDto.setContent(message.getContent());
// // 		// messageDto.getRoomId();  //messageDto.setRoomId(message.getRoomId());
// // 		// messageDto.getUser().getUserId(); //messageDto.setUser(UserDto.from(message.getUser()));
// // 		// messageDto.getCreatedAt();//messageDto.setCreatedAt(message.getCreatedAt());
// // 	}
// //
// // 	public static ChatMessageDto from(ChatMessage message) {
// // 		ChatMessageDto messageDto = new ChatMessageDto();
// // 		messageDto.setChatMessageId(message.getId());
// // 		messageDto.setRoomId(message.getChatRoom().getId());
// // 		messageDto.setContent(message.getContent());
// // 		messageDto.setCreatedAt(message.getCreatedAt());
// // 		messageDto.setUser(UserDto.from(message.getSender()));
// // 		return messageDto;
// // 	}
// //
// //
// // }
//
// import java.time.LocalDateTime;
//
// import com.ddalggak.finalproject.domain.message.entity.ChatMessage;
// import com.ddalggak.finalproject.domain.user.entity.User;
//
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
//
// @Getter
// @Setter
// @NoArgsConstructor
// public class ChatMessageDto {
// 	private Long chatMessageId;
// 	private String content;
// 	private Long roomId;
// 	private User user;
// 	// 유저리스트, 발신자, 수신자 필요
// 	private LocalDateTime createdAt;
// 	public ChatMessageDto(Long chatMessageId, String content, Long roomId, User user, LocalDateTime createdAt) {
// 		this.chatMessageId = chatMessageId;
// 		this.content = content;
// 		this.roomId = roomId;
// 		this.user = user;
// 		this.createdAt = createdAt;
// 	}
//
// 	// public static ChatMessageDto from(ChatMessage message) {
// 	// 	ChatMessageDto messageDto = new ChatMessageDto();
// 	// 	messageDto.setChatMessageId(message.());
// 	// 	messageDto.setRoomId(message.getChatRoom().getId());
// 	// 	messageDto.setContent(message.getContent());
// 	// 	messageDto.setCreatedAt(message.getCreatedAt());
// 	// 	messageDto.setUser(message.getSender());
// 	// 	return messageDto;
// 	// }
//
// }
