package com.ddalggak.finalproject.domain.ticket.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.comment.dto.CommentResponseDto;
import com.ddalggak.finalproject.domain.comment.entity.Comment;
import com.ddalggak.finalproject.domain.comment.repository.CommentRepository;
import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.domain.label.repository.LabelRepository;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.task.repository.TaskRepository;
import com.ddalggak.finalproject.domain.ticket.dto.TicketLabelRequestDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketRequestDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.repository.TicketRepository;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
	private final UserRepository userRepository;
	private final TaskRepository taskRepository;
	private final TicketRepository ticketRepository;
	private final CommentRepository commentRepository;
	private final LabelRepository labelRepository;

	// 티켓 등록
	@Transactional
	public ResponseEntity<?> createTicket(User user, TicketRequestDto ticketRequestDto) {
		//1. 이메일 확인
		validateUserByEmail(user.getEmail());
		//2. task가 존재하는지 확인
		Task task = validateTask(ticketRequestDto.getTaskId());
		//3. 티켓 생성
		Ticket ticket = Ticket.create(ticketRequestDto, task);
		//4. 티켓 저장
		ticketRepository.save(ticket);
		//5. return
		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);
	}

	// 티켓 상세조회
	@Transactional(readOnly = true)
	public ResponseEntity<TicketResponseDto> getTicket(Long ticketId, User user) {
		TicketResponseDto ticket = ticketRepository.findWithOrderedComments(ticketId);
		// validateTask(ticket.get);
		return ResponseEntity.ok(ticket);
	}

	// 티켓 수정하기 todo 유효성검증
	@Transactional
	public ResponseEntity<?> updateTicket(Long ticketId, TicketRequestDto ticketRequestDto, User user) {
		Ticket ticket = validateTicket(ticketId);
		Task task = ticket.getTask();
		//같은 transaction 안에 있기에 DB에 새로 접근하는 것이 아니며, dirty checking에 의해 task는 dynamic update를 따르도록 한다.
		task.deleteTicket(ticket);
		ticket.update(ticketRequestDto);
		task.addTicket(ticket);
		ticketRepository.save(ticket);
		return SuccessResponseDto.toResponseEntity(SuccessCode.UPDATED_SUCCESSFULLY);
	}

	// 티켓 삭제하기
	@Transactional //todo 삭제된 티켓에 대해서 task의 db일관성문제가 터진다.
	public ResponseEntity<?> deleteTicket(User user, Long ticketId) {
		user = validateUserByEmail(user.getEmail()); // todo validate 로직 다시 짜기
		// 1. 티켓 존재하는지 확인
		Ticket ticket = validateTicket(ticketId);
		//2. task 존재하는지 확인
		Task task = ticket.getTask();
		//3. task의 ticketList에서 ticket 지움
		task.deleteTicket(ticket);
		//4. ticket Repo에서 ticket 삭제함
		ticketRepository.delete(ticket);
		return SuccessResponseDto.toResponseEntity(SuccessCode.DELETED_SUCCESSFULLY);
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

	// User Email 유무 확인
	private User validateUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(
			() -> new CustomException(MEMBER_NOT_FOUND)
		);
	}
	// ticket 유효성 검사
	// private void validateTicket(Task task, Ticket ticket,  UserDetailsImpl userDetails, Long taskId, Long ticketId) {
	// // task에 해당 ticket이 있는지 검사
	// if (!ticket.getTicketId().equals(task.getTaskId()))
	// 	throw new CustomException(TICKET_NOT_FOUND);
	// // ticket 작성자와 요청자의 일치 여부 검사
	// // if (!ticket.getUserList().getUserId().equals(userDetails.getUser().getUserId()))
	// // 	throw new CustomException(UNAUTHORIZED_USER);
	// if (!ticket.getTicketId().equals(userDetails.getUser().getUserId()));
	// throw new CustomException(UNAUTHORIZED_USER);
	// 	// ticketId task의 ticketList 동일한 위치에
	// 	// if (!(task.getLabelLeadersList().equals(user.getEmail())) || task.getLabelLeadersList().contains(user.getEmail())) {
	//
	// 		// if (!ticket.getTicketId().equals(userDetails.getEmail()) || (!task.getTicketList().equals(taskId)))
	// 	// if (!ticket.getTicketId().equals(task.getTicketList()))
	// 	// 	throw new CustomException(TICKET_NOT_FOUND);
	// }
	// 티켓에 있는 댓글 가져오기
	// 권한 부여
	// private void validateExistUser(Task task, User user) {
	// 	if (!(task.getLabelLeadersList().equals(user.getEmail())) || task.getLabelLeadersList().contains(user.getEmail())) {
	// 		throw new CustomException(UNAUTHORIZED_USER);
	// 	}
	// }

	// 	if (!ticket.getTeamLeader().equals(userDetails.getUser().getUserId()))
	// 		// equals(userDetails.getUser().getUserId()))
	// 		throw new CustomException(UNAUTHORIZED_USER);
	// }
	private void validateTaskLeader(Task task, UserDetailsImpl userDetails) {
	}

	private void validateTeamLeader(Ticket ticket, UserDetailsImpl userDetails) {
	}

	private Label validateExistTeam(Label label, User user) {
		if (!label.getLabelLeader().contains(user.toString())) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		return label;
	}

	/*
	 * 임시 api
	 */
	@Transactional
	public ResponseEntity<?> completeTicket(User user, Long ticketId) {
		Ticket ticket = validateTicket(ticketId);
		if (ticket.getUser().getUserId().equals(user.getUserId())) {
			ticket.completeTicket();
		} else {
			throw new CustomException(UNAUTHORIZED_MEMBER);
		}
		;
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

}