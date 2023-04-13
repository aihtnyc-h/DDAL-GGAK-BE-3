package com.ddalggak.finalproject.domain.comment.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.comment.dto.CommentMapper;
import com.ddalggak.finalproject.domain.comment.dto.CommentRequestDto;
import com.ddalggak.finalproject.domain.comment.dto.CommentResponseDto;
import com.ddalggak.finalproject.domain.comment.entity.Comment;
import com.ddalggak.finalproject.domain.comment.repository.CommentRepository;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.repository.TicketRepository;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

	private final CommentMapper commentMapper;
	private final CommentRepository commentRepository;
	private final TicketRepository ticketRepository;
	private final UserRepository userRepository;

	// 댓글 작성
	@Transactional
	public ResponseEntity<?> createComment(User user, CommentRequestDto commentRequestDto) {
		// 유효성 검증
		user = validateUserByEmail(user.getEmail());
		Ticket ticket = TicketValidation(commentRequestDto.getTicketId());
		// comment 작성
		Comment comment = commentMapper.mapToEntity(user, ticket, commentRequestDto);
		commentRepository.save(comment);
		// 상태 반환
		List<CommentResponseDto> result = commentRepository.findAllByTicketOrderByCreatedAtDesc(ticket)
			.stream()
			.map(commentMapper::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	// 댓글 수정
	@Transactional
	public ResponseEntity<?> updateComment(User user, Long commentId, CommentRequestDto commentRequestDto) {
		// 유효성 검증
		validateUserByEmail(user.getEmail());
		Ticket ticket = TicketValidation(commentRequestDto.getTicketId());
		// comment 수정 메서드
		Comment comment = CommentValidation(commentId);
		comment.update(commentRequestDto);
		// 상태 반환
		List<CommentResponseDto> result = commentRepository.findAllByTicketOrderByCreatedAtDesc(ticket)
			.stream()
			.map(commentMapper::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	// 댓글 삭제
	@Transactional
	public ResponseEntity<?> deleteComment(UserDetailsImpl userDetails, Long commentId) {
		Comment comment = CommentValidation(commentId);
		Ticket ticket = TicketValidation(comment.getTicket().getTicketId());
		// checkValidation(ticket, comment, userDetails);
		if (!comment.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		// 삭제
		commentRepository.delete(comment);
		// 상태 반환
		List<CommentResponseDto> result = commentRepository.findAllByTicketOrderByCreatedAtDesc(ticket)
			.stream()
			.map(commentMapper::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	/* == 반복 로직 == */

	// Ticket 유무 확인
	private Ticket TicketValidation(Long ticketId) {
		return ticketRepository.findById(ticketId).orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));
	}

	// comment 유무 확인
	private Comment CommentValidation(Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
	}

	private User validateUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(
			() -> new CustomException(MEMBER_NOT_FOUND)
		);
	}

	// comment 유효성 검사
	private void checkValidation(Ticket ticket, Comment comment, UserDetailsImpl userDetails) {
		// ticket에 해당 comment가 있는지 검사
		if (!comment.getTicket().getTicketId().equals(ticket.getTicketId()))
			throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
		// comment 작성자와 요청자의 일치 여부 검사
		if (!comment.getUser().getUserId().equals(userDetails.getUser().getUserId()))
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
	}
}
