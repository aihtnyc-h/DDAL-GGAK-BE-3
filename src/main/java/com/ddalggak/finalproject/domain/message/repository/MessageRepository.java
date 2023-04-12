package com.ddalggak.finalproject.domain.message.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddalggak.finalproject.domain.message.entity.ChatMessage;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

	// List<ChatMessage> findAllByroomIdOrderBycreatedAtAsc(Long roomId);

	List<ChatMessage> findAllByRoomIdOrderByCreatedAtAsc(Long roomId);
}

