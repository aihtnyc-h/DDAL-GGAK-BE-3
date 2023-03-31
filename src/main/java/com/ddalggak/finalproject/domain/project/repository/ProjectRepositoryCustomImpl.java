package com.ddalggak.finalproject.domain.project.repository;

import static com.ddalggak.finalproject.domain.project.entity.QProject.*;
import static com.ddalggak.finalproject.domain.project.entity.QProjectUser.*;

import java.util.List;

import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;
import com.ddalggak.finalproject.domain.project.dto.QProjectBriefResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	//@Query("select new com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto(p.projectId, p.projectTitle, p.thumbnail) from Project p join p.projectUserList pu where pu.user.userId = :userId")
	@Override
	public List<ProjectBriefResponseDto> findProjectAllByUserId(Long userId) {
		return queryFactory.select(new QProjectBriefResponseDto(
				project.projectId,
				project.projectTitle,
				project.thumbnail
			))
			.from(project)
			.join(project.projectUserList, projectUser)
			.where(projectUser.user.userId.eq(userId))
			.fetch();
	}
}
