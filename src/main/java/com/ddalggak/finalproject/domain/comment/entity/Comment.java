package com.ddalggak.finalproject.domain.comment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ddalggak.finalproject.domain.comment.dto.CommentRequestDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class Comment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;
	@Column(nullable = false)
	private String comment; //logDescription 바꾸기
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticketId")
	private Ticket ticket;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	@Builder
	public Comment(User user, Ticket ticket, CommentRequestDto commentList) {
		this.commentId = getCommentId();
		this.user = user;
		this.ticket = ticket;
		this.comment = commentList.getComment();
	}

	public void update(CommentRequestDto commentRequestDto) {
		this.comment = commentRequestDto.getComment();
	}
}