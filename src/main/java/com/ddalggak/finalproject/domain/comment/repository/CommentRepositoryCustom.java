package com.ddalggak.finalproject.domain.comment.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.comment.entity.Comment;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;

public interface CommentRepositoryCustom {
	List<Comment> findAllByTicketOrderByCreatedAtDesc(Ticket ticket);
	List<Comment> findAllByTicketId(Long ticketId);
	void deleteAllByTicket(Ticket ticket);
}
