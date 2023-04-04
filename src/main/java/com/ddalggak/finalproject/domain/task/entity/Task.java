package com.ddalggak.finalproject.domain.task.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.task.dto.TaskRequestDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.global.entity.BaseEntity;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
@DynamicUpdate
public class Task extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long taskId;

	private String taskTitle;

	private int totalPriority;

	private int totalDifficulty;

	private LocalDate expiredAt;

	@Setter
	private String taskLeader;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Label> labelList = new ArrayList<>();

	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 500)
	private List<TaskUser> taskUserList = new ArrayList<>();

	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 500)
	private List<Ticket> ticketList = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "LABEL_LEADER", joinColumns = @JoinColumn(name = "user_email"))
	private Set<String> labelLeadersList = new HashSet<>();

	@QueryProjection
	public Task(Long id, String taskTitle, LocalDate expiredAt, String taskLeader) {
		this.taskId = id;
		this.taskTitle = taskTitle;
		this.expiredAt = expiredAt;
		this.taskLeader = taskLeader;
	}

	@Builder
	public Task(TaskRequestDto taskRequestDto, TaskUser taskUser, Project project) {
		taskTitle = taskRequestDto.getTaskTitle();
		expiredAt = taskRequestDto.getExpiredAt();
		taskLeader = taskUser.getUser().getEmail();
		addProject(project);
		addTaskUser(taskUser);
	}

	public static Task create(TaskRequestDto taskRequestDto, TaskUser taskUser, Project project) {
		return Task.builder()
			.taskRequestDto(taskRequestDto)
			.taskUser(taskUser)
			.project(project)
			.build();
	}

	//연관관계 편의 메소드

	public void addTaskUser(TaskUser taskUser) {
		taskUserList.add(taskUser);
		taskUser.addTask(this);
	}

	private void addProject(Project project) {
		this.project = project;
		project.addTask(this);
	}

	public void addLabel(Label label) {
		labelList.add(label);
	}

	public void addTicket(Ticket ticket) {
		ticketList.add(ticket);
		totalDifficulty += ticket.getDifficulty();
		totalPriority += ticket.getPriority();
	}

	public void deleteTicket(Ticket ticket) {
		ticketList.remove(ticket);
		totalDifficulty -= ticket.getDifficulty();
		totalPriority -= ticket.getPriority();
	}

}
