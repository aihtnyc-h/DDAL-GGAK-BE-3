package com.ddalggak.finalproject.domain.user.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.user.entity.User;

public interface UserRepositoryCustom {

	List<LabelUser> getUserFromLabelId(Long labelId);

	List<TaskUser> getTaskUserFromTaskId(Long taskId);

	List<User> getUserFromTaskId(Long taskId);

	List<User> getUserFromProjectId(Long projectId);

	List<ProjectUser> getProjectUserFromProjectId(Long projectId);

}
