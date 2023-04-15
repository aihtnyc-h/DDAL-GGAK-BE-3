package com.ddalggak.finalproject.domain.ticket.service;

import static com.ddalggak.finalproject.domain.ticket.entity.TicketStatus.*;
import static com.ddalggak.finalproject.global.error.ErrorCode.*;
import static org.springframework.http.ResponseEntity.*;

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
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> createTicket(User user,
		TicketRequestDto ticketRequestDto) {
		//1. task가 존재하는지 확인
		Task task = validateTask(ticketRequestDto.getTaskId());
		//1.5 ticket expiredAt의 유효성 검증
		if (ticketRequestDto.getTicketExpiredAt() != null &&
			task.getExpiredAt() != null &&
			ticketRequestDto.getTicketExpiredAt().isBefore(task.getExpiredAt())) {
			throw new IllegalArgumentException("티켓의 만료일은 task의 만료일보다 빠를 수 없습니다.");
		}
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
		// 유효성 검증
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
		return ok(ticketResponseDto);
	}

	// 티켓 수정하기
	@Transactional
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> updateTicket(Long ticketId,
		TicketRequestDto ticketRequestDto, User user) {
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
		return ok(ListWithTicketStatus);
	}

	// 티켓 삭제하기
	@Transactional
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> deleteTicket(User user, Long ticketId) {
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
		return ok(ListWithTicketStatus);
	}

	// 티켓 완료 처리
	@Transactional
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> completeTicket(User user, Long ticketId) {
		// 유효성 검사
		Ticket ticket = validateTicket(ticketId);
		// ticket의 status 검사
		if (ticket.getStatus() != REVIEW) {
			throw new CustomException(INVALID_TICKET_STATUS);
		} else if (
			ticket.getTask().getProject().getProjectLeader().equals(user.getEmail()) ||
				ticket.getTask().getTaskLeader().equals(user.getEmail()) ||
				(ticket.getLabel() != null && ticket.getLabel().getLabelLeader().equals(user.getEmail()))) {
			//dirty checking에 의해 ticket은 dynamic update를 따르도록 한다.
			ticket.completeTicket();
		} else {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		// 티켓 목록 갱신
		List<Ticket> ticketList = ticketRepository.findWithTaskId(ticket.getTask().getTaskId());
		Map<TicketStatus, List<TicketResponseDto>> ListWithTicketStatus = ticketMapper.toDtoMapWithStatus(ticketList);
		return ok(ListWithTicketStatus);
	}

	// 티켓 가져가기
	@Transactional
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> assignTicket(User user, Long ticketId) {
		//유효성 검사 & 0413 조영준 NPE 반환 고려 로직수정
		Ticket ticket = validateTicket(ticketId);
		if (ticket.getTask().getTaskUserList().stream().noneMatch(
			taskUser -> taskUser.getUser().getUserId().equals(user.getUserId())
		) || (ticket.getUser() != null && !(ticket.getUser().getEmail().equals(user.getEmail())))) {
			throw new CustomException(UNAUTHENTICATED_USER);
		} else if (ticket.getUser() != null) {
			ticket.unAssignTicket(); //
		} else {
			ticket.assignTicket(user);
		}
		// 결과 반환
		List<Ticket> ticketList = ticketRepository.findWithTaskId(ticket.getTask().getTaskId());
		Map<TicketStatus, List<TicketResponseDto>> ListWithTicketStatus = ticketMapper.toDtoMapWithStatus(ticketList);
		return ok(ListWithTicketStatus);
	}

	// ticket에 라벨 부여 todo 라벨값 0들어오면 라벨삭제
	@Transactional
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> getLabelForTicket(User user, Long ticketId,
		TicketLabelRequestDto ticketLabelRequestDto) {
		Ticket ticket = validateTicket(ticketId);
		if (ticket.getTask().getTaskUserList().stream().noneMatch(
			taskUser -> taskUser.getUser().getUserId().equals(user.getUserId())
		)) {
			throw new CustomException(UNAUTHORIZED_MEMBER);
		}
		if (ticketLabelRequestDto.labelId == 0) {
			ticket.deleteLabel();
		} else {
			Label label = validateLabel(ticketLabelRequestDto.labelId);
			ticket.addLabel(label);
		}
		List<Ticket> ticketList = ticketRepository.findWithTaskId(ticket.getTask().getTaskId());
		Map<TicketStatus, List<TicketResponseDto>> ListWithTicketStatus = ticketMapper.toDtoMapWithStatus(ticketList);
		return ok(ListWithTicketStatus);
	}

	// 티켓 이동
	@Transactional
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> movementTicket(User user, Long ticketId) {
		Ticket ticket = validateTicket(ticketId);
		if (ticket.getStatus() == DONE) {
			throw new CustomException(INVALID_TICKET_STATUS);
		} else if (ticket.getUser() != null && ticket.getUser().getUserId().equals(user.getUserId())) {
			ticket.movementTicket(ticket.getStatus());
		} else if (ticket.getUser() == null) {
			throw new CustomException(INVALID_TICKET_STATUS);
		} else {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		List<Ticket> ticketList = ticketRepository.findWithTaskId(ticket.getTask().getTaskId());
		Map<TicketStatus, List<TicketResponseDto>> ListWithTicketStatus = ticketMapper.toDtoMapWithStatus(ticketList);
		return ok(ListWithTicketStatus);
	}

	// 완료된 티켓을 review-enrollment 상태로 전환해야 한다.
	@Transactional
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> moveTicketToReview(User user, Long ticketId) {
		Ticket ticket = validateTicket(ticketId);
		if (ticket.getStatus().equals(DONE)) {
			throw new CustomException(INVALID_TICKET_STATUS);
		} else if (ticket.getStatus().equals(REVIEW)) {
			//다른 팀원의 로직상 TODO로 status 줘야 status가 in-progress로 들어간다.
			ticket.movementTicket(TODO);
		} else if (!ticket.getUser().getEmail().equals(user.getEmail())) {
			throw new CustomException(UNAUTHENTICATED_USER);
		} else {
			ticket.moveStatusToReview();
		}
		ticketRepository.save(ticket);
		List<Ticket> ticketList = ticketRepository.findWithTaskId(ticket.getTask().getTaskId());
		Map<TicketStatus, List<TicketResponseDto>> ListWithTicketStatus = ticketMapper.toDtoMapWithStatus(ticketList);
		return ok(ListWithTicketStatus);
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

	// label 우무 확인
	public Label validateLabel(Long labelId) {
		return labelRepository.findById(labelId).orElseThrow(() -> new CustomException(LABEL_NOT_FOUND));
	}

	private void validateExistMember(Project project, ProjectUser projectUser) {
		if (!project.getProjectUserList().contains(projectUser)) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
	}
}
