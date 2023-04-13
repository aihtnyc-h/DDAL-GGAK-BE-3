package com.ddalggak.finalproject.domain.project.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import com.ddalggak.finalproject.domain.project.dto.ProjectRequestDto;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Project extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectId;

	@Column(nullable = false)
	private String projectTitle;

	private String thumbnail;

	private String uuid; //todo 초대코드

	@Setter
	private String projectLeader;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 500)
	private List<ProjectUser> projectUserList = new ArrayList<>();

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 500)
	private List<Task> taskList = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "TASK_LEADER", joinColumns = @JoinColumn(name = "user_email"))
	private Set<String> taskLeadersList = new HashSet<>();

	@Builder
	public Project(ProjectRequestDto projectRequestDto, ProjectUser projectUser) {
		projectTitle = projectRequestDto.getProjectTitle();
		thumbnail = projectRequestDto.getThumbnail();
		projectLeader = projectUser.getUser().getEmail();
		addProjectUser(projectUser);
	}

	public static Project create(ProjectRequestDto projectRequestDto, ProjectUser projectUser) {
		return Project.builder()
			.projectRequestDto(projectRequestDto)
			.projectUser(projectUser)
			.build();
	}

	//연관관계 편의 메소드
	public void addProjectUser(ProjectUser projectUser) {
		projectUserList.add(projectUser);
		projectUser.addProject(this);
	}

	public void addTask(Task task) {
		taskList.add(task);
	}

	public void createUuid(String uuid) {
		this.uuid = uuid;
	}

	public void update(ProjectRequestDto projectRequestDto) {
		projectTitle = projectRequestDto.getProjectTitle();
		thumbnail = projectRequestDto.getThumbnail();
	}
}