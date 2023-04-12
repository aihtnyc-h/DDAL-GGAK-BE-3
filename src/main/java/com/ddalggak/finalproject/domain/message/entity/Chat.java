package com.ddalggak.finalproject.domain.message.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String chatId;
	private Long senderId;
	private String message;
	private Long workspaceId;
	private String inBoxId;
	private Integer unread;
	private LocalDateTime createdAt;
}
