package com.ddalggak.finalproject.domain.task.repository;

import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

import java.util.List;
import java.util.Optional;

import com.ddalggak.finalproject.domain.task.entity.Task;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Task> findTaskById(Long id) {
		Task result = queryFactory
			.selectFrom(task)
			.leftJoin(task.labelList, label)
			.leftJoin(task.ticketList, ticket).fetchJoin()
			.orderBy(ticket.createdAt.desc(), ticket.priority.desc())
			.where(task.taskId.eq(id))
			.distinct()
			.fetchOne();
		return Optional.ofNullable(result);
	}

	@Override
	public List<Task> findTaskByProject(Long projectId) {
		return queryFactory
			.selectFrom(task)
			.orderBy(task.createdAt.desc())
			.where(task.project.projectId.eq(projectId))
			.fetch();
	}

	@Override
	public Optional<Task> findTaskByLabelId(Long labelId) {
		Task result = queryFactory
			.selectFrom(task)
			.leftJoin(task.labelList, label)
			.where(label.labelId.eq(labelId))
			.fetchOne();

		return Optional.ofNullable(result);
	}
}