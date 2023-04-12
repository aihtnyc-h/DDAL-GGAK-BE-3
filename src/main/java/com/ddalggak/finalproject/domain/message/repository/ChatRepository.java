package com.ddalggak.finalproject.domain.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddalggak.finalproject.domain.message.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {
	//    @Query("{'workspaceId' : ?1, 'roomNum' : ?2}")
	//    Page<Chat> findByWorkspaceIdAndRoomNum(Pageable pageable, Long workspaceId, String inBoxId);
}
