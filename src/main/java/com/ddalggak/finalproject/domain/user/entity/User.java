package com.ddalggak.finalproject.domain.user.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
import com.ddalggak.finalproject.domain.user.role.UserRole;
import com.ddalggak.finalproject.global.entity.BaseTimeEntity;
import com.ddalggak.finalproject.global.oauth.entity.ProviderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Users")
@Builder

public class User extends BaseTimeEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = true)
	private String password;

	@Column(nullable = true)
	private String profile;

	@Column
	@Enumerated(value = EnumType.STRING)
	private UserRole role;

	@Column
	@Enumerated(value = EnumType.STRING)
	private ProviderType providerType;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<ProjectUser> projectUserList = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	List<TaskUser> taskUserList = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	List<LabelUser> labelUserList = new ArrayList<>();

	public static void updateOAuth(ProviderType providerType, String profile) {
		builder()
			.providerType(providerType)
			.profile(profile)
			.build();
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updateProfile(String profile) {
		this.profile = profile;
	}
}