package com.ddalggak.finalproject.domain.ticket.repository;

import static com.ddalggak.finalproject.domain.comment.entity.QComment.*;
import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.TicketMapper;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final TicketMapper ticketMapper;

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
		DateTemplate<LocalDate> formattedDate = Expressions.dateTemplate(LocalDate.class,
			"DATE_FORMAT({0},'%Y-%m-%d')", ticket.completedAt);
		return queryFactory
			.select(formattedDate, ticket.count())
			.from(ticket)
			.where(ticket.user.userId.eq(userId),
				getWithOneYear(condition.getDate())
			)
			.groupBy(formattedDate)
			.orderBy(ticket.completedAt.asc())
			.limit(365)
			.fetch()
			.stream()
			.map(tuple -> new DateTicket(tuple.get(0, String.class), tuple.get(1, Long.class)))
			.collect(Collectors.toList());
	}

	@Override
	public Slice<TicketResponseDto> getSlicedTicketCountByDate(TicketSearchCondition condition, Pageable pageable,
		Long userId) {
		List<TicketResponseDto> content = queryFactory
			.selectFrom(ticket)
			.where(ticket.user.userId.eq(userId),
				statusEq(condition.getStatus())
			)
			.orderBy(ticket.createdAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch()
			.stream()
			.map(ticketMapper::toDto)
			.collect(Collectors.toList());
		;
		boolean hasNext = false;
		if (content.size() > pageable.getPageSize()) {
			content.remove(pageable.getPageSize());
			hasNext = true;
		}
		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public List<Ticket> getTicketByUserId(Long userId) {
		return queryFactory
			.selectFrom(ticket)
			.where(ticket.user.userId.eq(userId))
			.fetch();
	}

	private BooleanExpression getWithOneYear(LocalDate localDate) {
		return localDate != null ?
			ticket.completedAt.between(localDate.minusYears(1).atStartOfDay(), localDate.plusDays(1).atStartOfDay()) :
			ticket.completedAt.between(LocalDate.now().minusYears(1).atStartOfDay(),
				LocalDate.now().plusDays(1).atStartOfDay());
	}

	private BooleanExpression statusEq(TicketStatus status) {
		return status != null ? ticket.status.eq(status) : null;
	}
}
