package com.ddalggak.finalproject.domain.ticket.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.domain.label.repository.LabelRepository;
import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.project.repository.ProjectRepository;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.task.repository.TaskRepository;
import com.ddalggak.finalproject.domain.ticket.dto.TicketLabelRequestDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketMapper;
import com.ddalggak.finalproject.domain.ticket.dto.TicketRequestDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.ticket.repository.TicketRepository;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketMapper ticketMapper;
	private final ProjectRepository projectRepository;
	private final TaskRepository taskRepository;
	private final TicketRepository ticketRepository;
	private final LabelRepository labelRepository;

	// 티켓 등록
	@Transactional
	public ResponseEntity<?> createTicket(User user, TicketRequestDto ticketRequestDto) {
		//1. task가 존재하는지 확인
		Task task = validateTask(ticketRequestDto.getTaskId());
		//2. 유효성 검증
		if (!(task.getProject().getProjectLeader().equals(user.getEmail()) ||
			task.getTaskLeader().equals(user.getEmail()) ||
			task.getLabelList().stream().anyMatch(label -> label.getLabelLeader().equals(user.getEmail()))
		)) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		//3. 티켓 생성
		Ticket ticket = Ticket.create(ticketRequestDto, task);
		//4. 티켓 저장
		ticketRepository.save(ticket);
		//5. task에 있는 ticketList return
		List<Ticket> ticketList = ticketRepository.findWithTaskId(task.getTaskId());
		Map<TicketStatus, List<TicketResponseDto>> ListWithTicketStatus = ticketMapper.toDtoMapWithStatus(ticketList);

		return ResponseEntity
			.status(201)
			.body(ListWithTicketStatus);
	}

	// 티켓 상세조회
	@Transactional(readOnly = true)
	public ResponseEntity<TicketResponseDto> getTicket(Long ticketId, Long taskId, User user) {
		// 유효성 검증 todo 더 나은 유효성 검증 방향이 있나 검사해보기
		Project project = projectRepository.findProjectByTaskId(taskId).orElseThrow(
			() -> new CustomException(PROJECT_NOT_FOUND)
		);
		ProjectUser projectUser = ProjectUser.create(project, user);
		validateExistMember(project, projectUser);
		// ticket 확인
		Ticket ticket = ticketRepository.findWithOrderedComments(ticketId).orElseThrow(
			() -> new CustomException(TICKET_NOT_FOUND)
		);
		// return
		TicketResponseDto ticketResponseDto = ticketMapper.toDto(ticket);
		return ResponseEntity.ok(ticketResponseDto);
	}

	// 티켓 수정하기
	@Transactional
	public ResponseEntity<?> updateTicket(Long ticketId, TicketRequestDto ticketRequestDto, User user) {
		// 유효성검증
		Ticket ticket = validateTicket(ticketId);
		Task task = validateTask(ticketRequestDto.getTaskId());
		if (!(task.getProject().getProjectLeader().equals(user.getEmail()) ||
			task.getTaskLeader().equals(user.getEmail()) ||
			(ticket.getLabel() != null && ticket.getLabel().getLabelLeader().equals(user.getEmail())))) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		//같은 transaction 안에 있기에 DB에 새로 접근하는 것이 아니며, dirty checking에 의해 task는 dynamic update를 따르도록 한다.
		task.deleteTicket(ticket);
		ticket.update(ticketRequestDto);
		task.addTicket(ticket);
		ticketRepository.save(ticket);
		// 수정 내역 반영된 ticketList 다시 조회
		List<Ticket> ticketList = ticketRepository.findWithTaskId(task.getTaskId());
		Map<TicketStatus, List<TicketResponseDto>> ListWithTicketStatus = ticketMapper.toDtoMapWithStatus(ticketList);
		return ResponseEntity.ok(ListWithTicketStatus);
	}

	// 티켓 삭제하기
	@Transactional
	public ResponseEntity<?> deleteTicket(User user, Long ticketId) {
		// 1. 티켓 존재하는지 확인
		Ticket ticket = validateTicket(ticketId);
		// 2. task 존재하는지 확인
		Task task = validateTask(ticket.getTask().getTaskId());
		// 3. 유효성 검사
		if (!(task.getProject().getProjectLeader().equals(user.getEmail()) ||
			task.getTaskLeader().equals(user.getEmail()) ||
			(ticket.getLabel() != null && ticket.getLabel().getLabelLeader().equals(user.getEmail())))) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		// 4. task의 ticketList에서 ticket 지움
		task.deleteTicket(ticket);
		// 5. ticket Repo에서 ticket 삭제함
		ticketRepository.delete(ticket);
		// 6. 남은 티켓 return
		List<Ticket> ticketList = ticketRepository.findWithTaskId(task.getTaskId());
		Map<TicketStatus, List<TicketResponseDto>> ListWithTicketStatus = ticketMapper.toDtoMapWithStatus(ticketList);
		return ResponseEntity.ok(ListWithTicketStatus);
	}

	@Transactional
	public ResponseEntity<?> completeTicket(User user, Long ticketId) {
		Ticket ticket = validateTicket(ticketId);
		if (ticket.getUser().getUserId().equals(user.getUserId())) {
			ticket.completeTicket();
		} else {
			throw new CustomException(UNAUTHORIZED_MEMBER);
		}
		ticketRepository.save(ticket);
		return SuccessResponseDto.toResponseEntity(SuccessCode.UPDATED_SUCCESSFULLY);
	}

	@Transactional
	public ResponseEntity<?> assignTicket(User user, Long ticketId) {
		Ticket ticket = validateTicket(ticketId);
		//todo task에 유저 있는지 검사
		ticket.assignTicket(user);
		return SuccessResponseDto.toResponseEntity(SuccessCode.UPDATED_SUCCESSFULLY);
	}

	@Transactional
	public ResponseEntity<?> getLabelForTicket(User user, Long ticketId, TicketLabelRequestDto ticketLabelRequestDto) {
		Ticket ticket = validateTicket(ticketId);
		//todo task에 유저 있는지 검사, 로직 수정 매우필요
		Label label = labelRepository.findById(ticketLabelRequestDto.labelId).orElseThrow(
			() -> new CustomException(LABEL_NOT_FOUND)
		);
		ticket.addLabel(label);
		return SuccessResponseDto.toResponseEntity(SuccessCode.UPDATED_SUCCESSFULLY);
	}
	// 티켓 이동
	@Transactional
	public ResponseEntity<?> movementTicket(User user, Long ticketId) {
		Ticket ticket = validateTicket(ticketId);
		if (ticket.getUser().getUserId().equals(user.getUserId())) {
			ticket.movementTicket(ticket.getStatus());
		} else {
			throw new CustomException(UNAUTHORIZED_MEMBER);
		}
		ticketRepository.save(ticket);
		return SuccessResponseDto.toResponseEntity(SuccessCode.UPDATED_SUCCESSFULLY);
	}

	private void validateExistMember(Project project, ProjectUser projectUser) {
		if (!project.getProjectUserList().contains(projectUser)) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
	}

	/* == 반복 로직 == */
	// task 유무 확인
	private Task validateTask(Long taskId) {
		return taskRepository.findById(taskId).orElseThrow(() -> new CustomException(TASK_NOT_FOUND));
	}

	// ticket 유무 확인
	public Ticket validateTicket(Long ticketId) {
		return ticketRepository.findById(ticketId).orElseThrow(() -> new CustomException(TICKET_NOT_FOUND));
	}

}