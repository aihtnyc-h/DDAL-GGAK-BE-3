package com.ddalggak.finalproject.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.user.role.UserRole;
import com.ddalggak.finalproject.global.entity.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Users")
@Builder
public class User extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String password;

	@Column(nullable = true)
	private String profile;

	@Column
	@Enumerated(value = EnumType.STRING)
	private UserRole role;
	@OneToMany(mappedBy = "user")
	List<ProjectUser> projectUserList = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	List<TaskUser> taskUserList = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	List<LabelUser> labelUserList = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	List<Ticket> ticketList = new ArrayList<>();

	public User(Long userId, String email, String nickname, String password, String profile, UserRole role) {
		this.userId = userId;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.profile = profile;
		this.role = role;
	}
}