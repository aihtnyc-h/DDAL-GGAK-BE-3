package com.ddalggak.finalproject.domain.message.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddalggak.finalproject.domain.message.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	List<ChatMessage> findAllByRoomIdOrderByCreatedAtAsc(Long roomId);
}
