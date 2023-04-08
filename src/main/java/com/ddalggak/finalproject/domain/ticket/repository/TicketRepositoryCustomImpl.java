package com.ddalggak.finalproject.domain.ticket.repository;

import static com.ddalggak.finalproject.domain.comment.entity.QComment.*;
import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

import java.util.List;
import java.util.Optional;

import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Ticket> findWithOrderedComments(Long ticketId) {
		Ticket result = queryFactory
			.selectFrom(ticket)
			.leftJoin(ticket.comment, comment1).fetchJoin()
			.where(ticket.ticketId.eq(ticketId))
			.orderBy(comment1.modifiedAt.desc())
			.fetchOne();
		return Optional.ofNullable(result);
	}

	@Override
	public List<Ticket> findWithTaskId(Long taskId) {
		return queryFactory
			.selectFrom(ticket)
			.join(ticket.task, task)
			.where(task.taskId.eq(taskId))
			.orderBy(ticket.createdAt.desc())
			.fetch();
	}

	@Override
	public List<Ticket> findWithLabelId(Long labelId) {
		return queryFactory
			.selectFrom(ticket)
			.join(ticket.label, label)
			.where(ticket.label.labelId.eq(labelId))
			.orderBy(ticket.createdAt.desc())
			.fetch();
	}
}
