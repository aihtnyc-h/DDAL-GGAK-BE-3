package com.ddalggak.finalproject.domain.message.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.ddalggak.finalproject.domain.user.entity.User;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class ChatRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chatRoomId;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "chat_room_users", joinColumns = @JoinColumn(name = "chat_room_id"))
	private List<User> userList;

	public List<User> getUserList() {
		return Collections.unmodifiableList(userList);
	}
	private Long workspaceId;

	private String inBoxId;

	@Builder
	public ChatRoom(List<User> userList, Long workspaceId, String inBoxId) {
		if (userList != null) {
			this.userList.addAll(userList);
		}
		this.workspaceId = workspaceId;
		this.inBoxId = inBoxId;
	}

	public void addUser(User user) {
		if (user != null) {
			userList.add(user);
		}
	}
}
