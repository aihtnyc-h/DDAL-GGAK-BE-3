package com.ddalggak.finalproject.domain.comment.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.comment.entity.Comment;
import com.ddalggak.finalproject.domain.comment.entity.QComment;
import com.ddalggak.finalproject.domain.ticket.entity.QTicket;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Comment> findAllByTicketOrderByCreatedAtDesc(Ticket ticket) {
		QComment comment = new QComment("comment");
		QTicket qTicket = QTicket.ticket;

		return queryFactory
			.selectFrom(comment)
			.join(comment.ticket, qTicket)
			.where(qTicket.eq(ticket))
			.orderBy(comment.createdAt.desc())
			.fetch();
	}

	@Override
	public List<Comment> findAllByTicketId(Long ticketId) {
		QComment comment = new QComment("comment");
		QTicket qTicket = QTicket.ticket;

		return queryFactory
			.selectFrom(comment)
			.join(comment.ticket, qTicket)
			.where(qTicket.ticketId.eq(ticketId))
			.orderBy(comment.createdAt.desc())
			.fetch();
	}

	@Override
	public void deleteAllByTicket(Ticket ticket) {
		QComment comment = new QComment("comment");
		QTicket qTicket = QTicket.ticket;
		BooleanExpression predicate = comment.ticket.eq(ticket);

		queryFactory
			.delete(comment)
			.where(predicate)
			.execute();
	}
}
