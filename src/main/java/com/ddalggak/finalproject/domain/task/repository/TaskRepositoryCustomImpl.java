package com.ddalggak.finalproject.domain.task.repository;

import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

import java.util.List;
import java.util.stream.Collectors;

import com.ddalggak.finalproject.domain.task.dto.TaskBriefResponseDto;
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

		Task result = queryFactory
			.selectFrom(task)
			.leftJoin(task.labelList, label)
			.leftJoin(task.ticketList, ticket).fetchJoin()
			.orderBy(ticket.createdAt.desc(), ticket.priority.desc())
			.where(task.taskId.eq(id))
			.distinct()
			.fetchOne();

		if (result == null) {
			throw new CustomException(ErrorCode.TASK_NOT_FOUND);
		}

		return new TaskResponseDto(result);
	}

	@Override
	public List<TaskBriefResponseDto> findTaskByProject(Long projectId) {
		List<Task> result = queryFactory
			.selectFrom(task)
			.orderBy(task.createdAt.desc())
			.where(task.project.projectId.eq(projectId))
			.fetch();

		return result
			.stream()
			.map(TaskBriefResponseDto::new)
			.collect(Collectors.toList());
	}
}
