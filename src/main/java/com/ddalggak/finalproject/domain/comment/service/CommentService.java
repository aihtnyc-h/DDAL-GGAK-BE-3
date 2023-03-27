package com.ddalggak.finalproject.domain.ticket.comment.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.ticket.comment.dto.CommentRequestDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.comment.entity.Comment;
import com.ddalggak.finalproject.domain.ticket.comment.repository.CommentRepository;
import com.ddalggak.finalproject.domain.ticket.repository.TicketRepository;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

	private final CommentRepository commentRepository;
	private final TicketRepository ticketRepository;

	// 댓글 작성
	public ResponseEntity<SuccessResponseDto> createComment(UserDetailsImpl userDetails,
		CommentRequestDto commentRequestDto) {
		System.out.println("---------commment = " + userDetails.getEmail());
		User user = userDetails.getUser();
		Ticket ticket = TicketValidation(commentRequestDto.getTicketId());
		// comment 작성
		Comment comment = new Comment(user, ticket, commentRequestDto);
		commentRepository.save(comment);
		// 상태 반환
		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);
	}
	// 댓글 수정
	public ResponseEntity<SuccessResponseDto> updateComment(UserDetailsImpl userDetails, Long commentId, Long ticketId,
		CommentRequestDto commentRequestDto) {
		Ticket ticket = TicketValidation(ticketId);
		Comment comment = getComment(commentId);
		checkValidation(ticket, comment, userDetails);
		// 삭제
		commentRepository.save(comment);
		// 상태 반환
		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);
	}
	// 댓글 삭제
	public ResponseEntity<SuccessResponseDto> deleteComment(UserDetailsImpl userDetails, Long commentId, Long ticketId) {
		Ticket ticket = TicketValidation(ticketId);
		Comment comment = getComment(commentId);
		checkValidation(ticket, comment, userDetails);
		// 삭제
		commentRepository.delete(comment);
		// 상태 반환
		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);
	}

	/* == 반복 로직 == */

	// Ticket 유무 확인
	private  Ticket TicketValidation(Long ticketId) {
		return ticketRepository.findById(ticketId).orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));
	}
	// comment 유무 확인
	private Comment getComment(Long commentId) {
		return commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
	}

	// comment 유효성 검사
	private void checkValidation(Ticket ticket, Comment comment, UserDetailsImpl userDetails) {
		// post에 해당 comment가 있는지 검사
		if(!comment.getTicket().getTicketId().equals(ticket.getTicketId()))
			throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
		// comment 작성자와 요청자의 일치 여부 검사
		if(!comment.getUser().getUserId().equals(userDetails.getUser().getUserId()))
			throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
	}
}
