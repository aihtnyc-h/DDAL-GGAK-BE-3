package com.ddalggak.finalproject.domain.label.repository;

import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;

import java.util.List;
import java.util.stream.Collectors;

import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.label.entity.Label;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LabelRepositoryCustomImpl implements LabelRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<LabelResponseDto> findByTaskId(Long taskId) {
		//label의 field가 얼마 없어서 label 조회하고 dto변경함

		List<Label> result = queryFactory
			.selectFrom(label)
			.leftJoin(label.task, task)
			.where(label.task.taskId.eq(taskId))
			.fetch();
		return result.stream().map(LabelResponseDto::new).collect(Collectors.toList());

	}
}
