package com.ddalggak.finalproject.domain.task.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ddalggak.finalproject.domain.task.dto.TaskUserRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "Task_User")
@NoArgsConstructor
@AllArgsConstructor
public class TaskUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "UserId")
	private User user;

	@ManyToOne(cascade = CascadeType.ALL) //todo 오류 발생하면 여기임
	@JoinColumn(name = "TaskId")
	private Task task;

	public static TaskUser create(TaskUserRequestDto taskUserRequestDto) {
		return TaskUser.builder()
			.user(taskUserRequestDto.getUser())
			.build();
	}

	public static TaskUser create(Task task, User user) {
		return TaskUser.builder()
			.task(task)
			.user(user)
			.build();
	}

	public void addTask(Task task) {
		this.task = task;
	}

	@Override
	public boolean equals(Object obj) {
		TaskUser taskUser = (TaskUser)obj;
		return this.getUser().getUserId().equals(taskUser.getUser().getUserId());
	}
}