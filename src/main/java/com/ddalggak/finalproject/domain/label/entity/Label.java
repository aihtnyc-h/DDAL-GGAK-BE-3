package com.ddalggak.finalproject.domain.label.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;

import com.ddalggak.finalproject.domain.label.dto.LabelRequestDto;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Label extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long labelId;

	private String labelTitle;
	@Setter
	private String labelLeader;
	@ManyToOne
	@JoinColumn(name = "task_id")
	private Task task;

	@OneToMany(mappedBy = "label", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 100)
	private List<LabelUser> labelUserList = new ArrayList<>();

	@OneToMany(mappedBy = "label", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Ticket> tickets = new ArrayList<>();

	@Builder
	public Label(LabelRequestDto labelRequestDto, LabelUser labelUser, Task task) {
		labelTitle = labelRequestDto.getLabelTitle();
		labelLeader = labelUser.getUser().getEmail();
		addTask(task);
		addLabelUser(labelUser);
	}

	public static Label create(LabelRequestDto labelRequestDto, LabelUser labelUser, Task task) {
		return Label.builder()
			.labelRequestDto(labelRequestDto)
			.labelUser(labelUser)
			.task(task)
			.build();
	}

	private void addTask(Task task) {
		this.task = task;
		task.addLabel(this);
	}

	private void addLabelUser(LabelUser labelUser) {
		labelUserList.add(labelUser);
		labelUser.addLabel(this);
	}

	public void addTicket(Ticket ticket) {
		tickets.add(ticket);
	}
}
