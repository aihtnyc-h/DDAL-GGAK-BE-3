package com.ddalggak.finalproject.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;
import com.ddalggak.finalproject.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {

	//나중에 projectUser에 기능 더 붙으면, 아니면 user의 정보조회까지 필요하면 fetch join이 쿼리 절약해줄 수 있음
	@Query("select new com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto(p.projectId, p.projectTitle, p.thumbnail) from Project p join p.projectUserList pu where pu.user.userId = :userId")
	List<ProjectBriefResponseDto> findAllinDtoByUserId(Long userId);

	@Modifying
	@Query("update Project p set p.projectTitle = :projectTitle, p.thumbnail = :thumbnail where p.projectId = :projectId")
	void update(@Param("projectTitle") String projectTitle, @Param("thumbnail") String thumbnail,
		Long projectId);
}
