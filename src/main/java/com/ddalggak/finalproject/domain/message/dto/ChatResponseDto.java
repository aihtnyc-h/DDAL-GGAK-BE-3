package com.ddalggak.finalproject.domain.message.dto;

import java.time.LocalDateTime;

import com.ddalggak.finalproject.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ChatResponseDto {
	private Long roomId;
	private String email;
	private Long senderId;
	private Long receptionId;
	private LocalDateTime roomExpiredAt;
}
