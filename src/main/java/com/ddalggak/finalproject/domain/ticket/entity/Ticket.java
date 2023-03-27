package com.ddalggak.finalproject.domain.ticket.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.DynamicUpdate;

import com.ddalggak.finalproject.domain.comment.entity.Comment;
import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.ticket.dto.TicketRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@DynamicUpdate
public class Ticket extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ticketId;

	// 티켓 제목 notnull
	private String ticketTitle;
	// 티켓 내용 notnull
	private String ticketDescription;
	// 중요도 null 허용 -> int 로 변경 필요
	private int priority;
	// 난이도  null 허용 -> int 로 변경 필요
	private int difficulty;
	// 태그(이름 변경 해야함)  null 허용
	private LocalDate expiredAt;
	private LocalDate completedAt;
	// task 연관관계
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_Id")
	private Task task;

	// user 연관관계 // FE에서 user -> onwer 로 변경요청
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private User user;

	@Enumerated(value = EnumType.STRING)
	private TicketStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "label_Id")
	private Label label;
	// 댓글 연관관계
	@OneToMany(mappedBy = "ticket", cascade = CascadeType.REMOVE)
	private List<Comment> comment = new ArrayList<>();

	@Builder
	public Ticket(TicketRequestDto ticketRequestDto, Task task) {
		ticketTitle = ticketRequestDto.getTicketTitle();
		ticketDescription = ticketRequestDto.getTicketDescription();
		priority = ticketRequestDto.getPriority();
		difficulty = ticketRequestDto.getDifficulty();
		expiredAt = ticketRequestDto.getTicketExpiredAt();
		status = TicketStatus.TODO;
		addTask(task);
	}

	public void update(TicketRequestDto ticketRequestDto) {
		this.ticketTitle = ticketRequestDto.getTicketTitle();
		this.ticketDescription = ticketRequestDto.getTicketDescription();
		this.priority = ticketRequestDto.getPriority();
		this.difficulty = ticketRequestDto.getDifficulty();
		this.expiredAt = ticketRequestDto.getTicketExpiredAt();
	}

	public static Ticket create(TicketRequestDto ticketRequestDto, Task task) {
		return Ticket.builder()
			.ticketRequestDto(ticketRequestDto)
			.task(task)
			.build();
	}

	public void completeTicket() {
		status = TicketStatus.DONE;
		completedAt = LocalDate.now();
	}

	public void assignTicket(User user) {
		this.user = user;
	}

	/**
	 * 양방향 연관관계 메소드
	 */

	public void addLabel(Label label) {
		label.addTicket(this);
		this.label = label;
	}

	public void addTask(Task task) {
		this.task = task;
		task.addTicket(this);
	}
	public void movementTicket(TicketStatus ticketStatus) {
		if (ticketStatus == TicketStatus.TODO) {
			this.status = TicketStatus.IN_PROGRESS;
		} else if (ticketStatus == TicketStatus.IN_PROGRESS) {
			this.status = TicketStatus.TODO;
		}
	}
}
