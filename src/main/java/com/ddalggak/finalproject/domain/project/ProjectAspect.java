package com.ddalggak.finalproject.domain.project;

import java.util.List;
import java.util.UUID;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class ProjectAspect {
	private final ProjectRepository projectRepository;

	@Scheduled(fixedRate = 86400000) // 현재는 10초마다 수행
	public void generateNewProjectInviteCode() {
		List<Project> projects = projectRepository.findAll();
		for (Project project : projects) {
			String newProjectInviteCode = UUID.randomUUID().toString();
			project.setInviteCode(newProjectInviteCode);
			projectRepository.save(project);
		}
	}
}