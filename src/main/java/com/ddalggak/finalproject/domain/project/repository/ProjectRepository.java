package com.ddalggak.finalproject.domain.project.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ddalggak.finalproject.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query("select p from Project p join fetch p.projectUserList pu where pu.user.userId = :userId")
	List<Project> findAllByUserId(Long userId);

	@Modifying
	@Lock(LockModeType.OPTIMISTIC)
	@Query("update Project p set p.projectTitle = :projectTitle, p.thumbnail = :thumbnail where p.projectId = :projectId")
	void update(@Param("projectTitle") String projectTitle, @Param("thumbnail") String thumbnail,
		Long projectId);
}
