package com.ddalggak.finalproject.domain.ticket.repository;

import static com.ddalggak.finalproject.domain.comment.entity.QComment.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

import java.util.List;
import java.util.stream.Collectors;

import com.ddalggak.finalproject.domain.task.entity.QTask;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.ticket.dto.TicketMapper;
import com.ddalggak.finalproject.domain.ticket.dto.TicketRequestDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final TicketMapper ticketMapper;

	@Override
	public TicketResponseDto findWithOrderedComments(Long ticketId) {
		Task task = queryFactory
			.selectFrom(QTask.task)
			.join(QTask.task.ticketList, ticket)
			.where(ticket.ticketId.eq(ticketId))
			.fetchOne();
		if (task == null) {
			throw new CustomException(ErrorCode.TASK_NOT_FOUND);
		}

		Ticket result = queryFactory
			.selectFrom(ticket)
			.leftJoin(ticket.comment, comment1).fetchJoin()
			.where(ticket.ticketId.eq(ticketId))
			.orderBy(comment1.modifiedAt.desc())
			.fetchOne();
		if (result == null) {
			throw new CustomException(ErrorCode.TICKET_NOT_FOUND);
		}
		return ticketMapper.toDto(result);
	}

	@Override
	public List<TicketResponseDto> findWithTaskId(Long taskId) {
		return queryFactory
			.selectFrom(ticket)
			.join(ticket.task, QTask.task)
			.where(QTask.task.taskId.eq(taskId))
			.orderBy(ticket.createdAt.desc())
			.fetch()
			.stream()
			.map(ticketMapper::toDto)
			.collect(Collectors.toList());
	}

	private BooleanExpression isTicketPriorityChanged(TicketRequestDto ticketRequestDto) {
		return ticket.priority.eq(ticketRequestDto.getPriority());
	}

	private BooleanExpression isTicketDifficultyChanged(TicketRequestDto ticketRequestDto) {
		return ticket.difficulty.eq(ticketRequestDto.getDifficulty());
	}

	private BooleanExpression isTicketExpiredAtChanged(TicketRequestDto ticketRequestDto) {
		return ticket.expiredAt.eq(ticketRequestDto.getTicketExpiredAt());
	}

	private BooleanExpression isTicketTitleChanged(TicketRequestDto ticketRequestDto) {
		return ticket.ticketTitle.eq(ticketRequestDto.getTicketTitle());
	}

	private BooleanExpression isTicketDescriptionChanged(TicketRequestDto ticketRequestDto) {
		return ticket.ticketDescription.eq(ticketRequestDto.getTicketDescription());
	}

}