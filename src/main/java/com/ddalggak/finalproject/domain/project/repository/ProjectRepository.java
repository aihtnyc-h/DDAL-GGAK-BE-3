package com.ddalggak.finalproject.domain.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddalggak.finalproject.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {
	Optional<Project> findByUuid(String inviteCode);
}
