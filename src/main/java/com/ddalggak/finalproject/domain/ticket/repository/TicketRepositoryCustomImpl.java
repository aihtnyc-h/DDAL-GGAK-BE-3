package com.ddalggak.finalproject.domain.ticket.repository;

import static com.ddalggak.finalproject.domain.comment.entity.QComment.*;
import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.QDateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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

	@Override
	public List<DateTicket> getCompletedTicketCountByDate(TicketSearchCondition condition, Long userId) {
		return queryFactory
			.select(new QDateTicket(
				ticket.completedAt.as("date"),
				ticket.count().as("completedTicket")
			))
			.from(ticket)
			.where(ticket.user.userId.eq(userId),
				getWithOneYear(condition.getDate())
			)
			.groupBy(ticket.completedAt)
			.orderBy(ticket.completedAt.asc())
			.fetch();
	}

	@Override
	public Slice<TicketResponseDto> getSlicedTicketCountByDate(TicketSearchCondition condition, Pageable pageable,
		Long userId) {
		List<TicketResponseDto> content = queryFactory
			.select(Projections.constructor(
				TicketResponseDto.class,
				ticket.ticketId,
				ticket.ticketTitle.as("title"),
				ticket.ticketDescription.as("description"),
				ticket.status,
				ticket.priority,
				ticket.difficulty,
				ticket.user.email.as("assigned"),
				ticket.expiredAt,
				ticket.completedAt,
				ticket.label.labelTitle.as("label")
			))
			.from(ticket)
			.where(ticket.user.userId.eq(userId),
				statusEq(condition.getStatus())
			)
			.orderBy(ticket.createdAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();
		;
		boolean hasNext = false;
		if (content.size() > pageable.getPageSize()) {
			content.remove(pageable.getPageSize());
			hasNext = true;
		}
		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public List<Ticket> getTicketByUserId(TicketSearchCondition condition, Long userId) {
		return queryFactory
			.selectFrom(ticket)
			.where(ticket.user.userId.eq(userId),
				statusEq(condition.getStatus())
			)
			.orderBy(ticket.createdAt.desc())
			.fetch();

	}

	private BooleanExpression getWithOneYear(LocalDate localDate) {
		return localDate != null ? ticket.completedAt.between(localDate.minusYears(1), localDate) : null;
	}

	private BooleanExpression statusEq(TicketStatus status) {
		return status != null ? ticket.status.eq(status) : null;
	}
}
