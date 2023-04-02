package com.ddalggak.finalproject.domain.project.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectRequestDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectResponseDto;

public interface ProjectRepositoryCustom {

	List<ProjectBriefResponseDto> findProjectAllByUserId(Long userId);

	ProjectResponseDto findDtoByProjectId(Long projectId);

	void update(Long projectId, ProjectRequestDto projectRequestDto);

}
