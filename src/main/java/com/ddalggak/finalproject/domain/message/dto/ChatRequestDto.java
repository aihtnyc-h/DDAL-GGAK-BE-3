package com.ddalggak.finalproject.domain.message.dto;

import java.time.LocalDateTime;

import com.ddalggak.finalproject.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequestDto {
	private Long roomId;
	private String email;
	private Long senderId;
	private Long receptionId;
	private LocalDateTime roomExpiredAt;
}