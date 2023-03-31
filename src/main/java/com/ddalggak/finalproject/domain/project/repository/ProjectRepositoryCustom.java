package com.ddalggak.finalproject.domain.project.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;

public interface ProjectRepositoryCustom {

	List<ProjectBriefResponseDto> findProjectAllByUserId(Long userId);
}
