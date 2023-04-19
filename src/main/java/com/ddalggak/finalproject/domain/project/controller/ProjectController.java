package com.ddalggak.finalproject.domain.project.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectInviteCodeDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectRequestDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectResponseDto;
import com.ddalggak.finalproject.domain.project.service.ProjectService;
import com.ddalggak.finalproject.domain.task.dto.TaskSearchCondition;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.ddalggak.finalproject.global.aop.ExecutionTimer;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;
import com.ddalggak.finalproject.global.validation.RequestId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Project Controller", description = "프로젝트 관련 API입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "201", description = "프로젝트 생성 성공"),
			@ApiResponse(responseCode = "403", description = "권한 없음")
		})
	@Operation(summary = "프로젝트 생성", description = "api for creating project", parameters = {
		@Parameter(name = "projectRequestDto", description = "프로젝트 생성에 필요한 정보입니다.", required = true)
	})
	@PostMapping("/project")
	@ExecutionTimer
	public ResponseEntity<List<ProjectBriefResponseDto>> createProject(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestPart(value = "thumbnail", required = false) MultipartFile image,
		@Valid @RequestPart(value = "data") ProjectRequestDto projectRequestDto) throws IOException {
		return projectService.createProject(userDetails.getUser(), image, projectRequestDto);
	}

	@Operation(summary = "프로젝트 전체조회", description = "api for view all projects")
	@GetMapping("/projects")
	@ExecutionTimer
	public ResponseEntity<List<ProjectBriefResponseDto>> viewProjectAll(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return projectService.viewProjectAll(userDetails.getUser());
	}

	@Operation(summary = "프로젝트 단건조회", description = "api for view one project", parameters = {
		@Parameter(name = "projectId", description = "조회할 프로젝트의 id입니다.", required = true)
	})
	@GetMapping("/project/{projectId}")
	@ExecutionTimer
	public ResponseEntity<ProjectResponseDto> viewProject(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestId @PathVariable Long projectId) {
		return projectService.viewProject(userDetails.getUser(), projectId);
	}

	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "프로젝트 참여 성공"),
			@ApiResponse(responseCode = "400", description = "프로젝트 참여 실패 - 이미 있는 사용자"),
			@ApiResponse(responseCode = "403", description = "프로젝트 참여 실패 - 권한 없음"),
			@ApiResponse(responseCode = "404", description = "프로젝트 참여 실패(존재하지 않는 프로젝트)")
		}
	)
	@Operation(summary = "프로젝트 참여", description = "api for join project")
	@PostMapping("/project/join")
	@ExecutionTimer
	public ResponseEntity<List<ProjectBriefResponseDto>> joinProject(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody ProjectInviteCodeDto projectInviteCodeRequestDto) {
		return projectService.joinProject(userDetails.getUser(),
			projectInviteCodeRequestDto.getProjectInviteCode());
	}

	@Operation(summary = "프로젝트 수정", description = "api for update project")
	@PostMapping("/project/{projectId}/settings")
	@ExecutionTimer
	public ResponseEntity<List<ProjectBriefResponseDto>> updateProject(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long projectId,
		@RequestPart(value = "thumbnail", required = false) MultipartFile image,
		@Valid @RequestPart(value = "data", required = false) ProjectRequestDto projectRequestDto) throws IOException {
		return projectService.updateProject(userDetails.getUser(), projectId, image, projectRequestDto);
	}

	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "프로젝트 삭제 성공"),
			@ApiResponse(responseCode = "400", description = "프로젝트 삭제 실패"),
			@ApiResponse(responseCode = "403", description = "프로젝트 삭제 권한 없음"),
			@ApiResponse(responseCode = "404", description = "프로젝트 삭제 실패(존재하지 않는 프로젝트)")
		}
	)
	@Operation(summary = "프로젝트 삭제", description = "api for delete project")
	@DeleteMapping("/project/{projectId}")
	@ExecutionTimer
	public ResponseEntity<List<ProjectBriefResponseDto>> deleteProject(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long projectId) {
		return projectService.deleteProject(userDetails.getUser(), projectId);
	}

	@Operation(summary = "프로젝트 참여자 조회", description = "api for view project members")
	@GetMapping("/project/{projectId}/users")
	@ExecutionTimer
	public ResponseEntity<List<UserResponseDto>> viewProjectUsers(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long projectId,
		TaskSearchCondition condition) {
		return projectService.viewProjectUsers(userDetails.getUser(), projectId, condition);
	}

	@Operation(summary = "프로젝트 참여자 삭제", description = "api for delete project member")
	@DeleteMapping("/project/{projectId}/user/{userId}")
	@ExecutionTimer
	public ResponseEntity<List<UserResponseDto>> deleteProjectUser(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long projectId,
		@PathVariable Long userId) {
		return projectService.deleteProjectUser(userDetails.getUser(), projectId, userId);
	}

	@Operation(summary = "프로젝트 초대 코드 생성", description = "api for inviting a user to a project")
	@PostMapping("/project/{projectId}/inviteCode")
	@ExecutionTimer
	public ResponseEntity<?> getInviteCode(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long projectId) {
		return projectService.getInviteCode(userDetails.getUser(), projectId);
	}

	@Operation(summary = "프로젝트 사용자 초대", description = "api for inviting a user to a project")
	@PostMapping("/project/{projectId}/invite")
	@ExecutionTimer
	public ResponseEntity<?> inviteProjectUser(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long projectId,
		@RequestBody List<String> emails) {
		return projectService.inviteProjectUser(userDetails.getUser(), projectId, emails);
	}
}
