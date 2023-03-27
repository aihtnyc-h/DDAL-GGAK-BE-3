package com.ddalggak.finalproject.domain.project.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ddalggak.finalproject.domain.project.dto.ProjectUserRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;

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

	@ManyToOne
	@JoinColumn(name = "UserId")
	private User user;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ProjectId")
	private Project project;

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
