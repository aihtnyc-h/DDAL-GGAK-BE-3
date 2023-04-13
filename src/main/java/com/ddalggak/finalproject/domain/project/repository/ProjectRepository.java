package com.ddalggak.finalproject.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddalggak.finalproject.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {
}
