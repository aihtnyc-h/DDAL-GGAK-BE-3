package com.ddalggak.finalproject.domain.label.repository;

import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;

import java.util.List;

import com.ddalggak.finalproject.domain.label.entity.Label;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LabelRepositoryCustomImpl implements LabelRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Label> findByTaskId(Long taskId) {
		return queryFactory
			.selectFrom(label)
			.leftJoin(label.task, task)
			.where(label.task.taskId.eq(taskId))
			.fetch();
	}
}
