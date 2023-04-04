package com.ddalggak.finalproject.domain.task.repository;

import static com.ddalggak.finalproject.domain.task.entity.QTask.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

import com.ddalggak.finalproject.domain.task.dto.TaskResponseDto;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.ticket.dto.TicketMapper;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final TicketMapper ticketMapper;

	@Override
	public TaskResponseDto findTaskById(Long id) {

		Task result = queryFactory.selectFrom(task)
			.leftJoin(task.ticketList, ticket).fetchJoin()
			.orderBy(ticket.createdAt.desc(), ticket.priority.desc())
			.where(task.taskId.eq(id))
			.fetchOne();
		if (result == null) {
			throw new CustomException(ErrorCode.TASK_NOT_FOUND);
		}
		return new TaskResponseDto(result);
	}
}
