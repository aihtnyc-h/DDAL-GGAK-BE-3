package com.ddalggak.finalproject.domain.ticket.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
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
	private String assigned;
	// 마감 날짜  null 허용 -> 최신 생성순으로
	private LocalDate ticketExpiredAt;
	@Setter
	private String labelLeader;

	// task 연관관계
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "taskId")
	private Task task;

	// user 연관관계 // FE에서 user -> onwer 로 변경요청
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	@Column(nullable = true)
	@Enumerated(value = EnumType.STRING)
	private TicketStatus status;
	// @OneToMany(mappedBy = "ticket")
	// private List<User> User = new ArrayList<>();

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
	private List<Label> labelList = new ArrayList<>();
	// 댓글 연관관계
	@OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
	private List<Comment> comment = new ArrayList<>();

	public Ticket(TicketRequestDto ticketRequestDto, User user, List<Comment> commentList) {
		this.ticketTitle = ticketRequestDto.getTicketTitle();
		this.ticketDescription = ticketRequestDto.getTicketDescription();
		this.priority = ticketRequestDto.getPriority();
		this.difficulty = ticketRequestDto.getDifficulty();
		this.assigned = ticketRequestDto.getAssigned();
		this.ticketExpiredAt = ticketRequestDto.getTicketExpiredAt();
		this.comment = commentList;
	}

	@Builder
	public Ticket(TicketRequestDto ticketRequestDto, Task task) {
		this.ticketTitle = ticketRequestDto.getTicketTitle();
		this.ticketDescription = ticketRequestDto.getTicketDescription();
		this.priority = ticketRequestDto.getPriority();
		this.difficulty = ticketRequestDto.getDifficulty();
		this.assigned = ticketRequestDto.getAssigned();
		this.ticketExpiredAt = ticketRequestDto.getTicketExpiredAt();
		this.task = task;
	}

	public void update(TicketRequestDto ticketRequestDto) {
		this.ticketTitle = ticketRequestDto.getTicketTitle();
		this.ticketDescription = ticketRequestDto.getTicketDescription();
		this.priority = ticketRequestDto.getPriority();
		this.difficulty = ticketRequestDto.getDifficulty();
		this.assigned = ticketRequestDto.getAssigned();
		this.ticketExpiredAt = ticketRequestDto.getTicketExpiredAt();
		this.comment = getComment();
	}


	public static Ticket create(TicketRequestDto ticketRequestDto, Task task) {
		return Ticket.builder()
			.ticketRequestDto(ticketRequestDto)
			.task(task)
			.build();
	}
}