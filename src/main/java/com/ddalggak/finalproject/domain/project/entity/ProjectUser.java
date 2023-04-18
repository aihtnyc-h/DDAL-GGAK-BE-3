package com.ddalggak.finalproject.domain.project.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ddalggak.finalproject.domain.project.dto.ProjectUserRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Project_User")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "ProjectId")
	private Project project;

	@QueryProjection
	public ProjectUser(User user, Project project) {
		this.user = user;
		this.project = project;
	}

	public static ProjectUser create(ProjectUserRequestDto projectUserDto) {
		return ProjectUser.builder()
			.user(projectUserDto.getUser())
			.build();
	}

	public static ProjectUser create(Project project, User user) {
		return ProjectUser.builder()
			.project(project)
			.user(user)
			.build();
	}

	public void addProject(Project project) {
		this.project = project;
	}

	@Override
	public boolean equals(Object obj) {
		ProjectUser projectUser = (ProjectUser)obj;
		return this.getUser().getUserId().equals(projectUser.getUser().getUserId());
	}
}
