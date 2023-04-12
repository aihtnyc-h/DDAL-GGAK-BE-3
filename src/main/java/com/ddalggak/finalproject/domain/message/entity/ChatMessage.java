// package com.ddalggak.finalproject.domain.message.entity;
//
// import java.time.LocalDateTime;
//
// import javax.persistence.Column;
// import javax.persistence.Entity;
// import javax.persistence.FetchType;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;
// import javax.persistence.JoinColumn;
// import javax.persistence.ManyToOne;
// import javax.persistence.Table;
//
// import com.ddalggak.finalproject.domain.user.entity.User;
//
// @Entity
// @Table(name = "chat_message")
// public class ChatMessage {
//
// 	@Id
// 	@GeneratedValue(strategy = GenerationType.IDENTITY)
// 	private Long chatMessageId;
//
// 	@Column(nullable = false)
// 	private String content;
//
// 	@Column(nullable = false)
// 	private Long roomId;
//
// 	@ManyToOne(fetch = FetchType.LAZY)
// 	@JoinColumn(name = "user_id", nullable = false)
// 	private User user;
//
// 	@Column(nullable = false)
// 	private LocalDateTime createdAt;
//
// 	public ChatMessage() {
// 		this.chatMessageId = chatMessageId;
// 		this.content = content;
// 		this.roomId = roomId;
// 		this.user = user;
// 		this.createdAt = createdAt;
// 	}
// 	public Long getChatMessageId() {
// 		return chatMessageId;
// 	}
//
// 	public LocalDateTime getCreatedAt() {
// 		return createdAt;
// 	}
// }
