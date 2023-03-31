package com.ddalggak.finalproject.domain.review.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ddalggak.finalproject.domain.review.dto.ReviewRequestDto;
import com.ddalggak.finalproject.domain.review.entity.Review;
import com.ddalggak.finalproject.domain.review.repository.ReviewCommentRepository;
import com.ddalggak.finalproject.domain.review.repository.ReviewRepository;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.repository.TicketRepository;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final TicketRepository ticketRepository;
	private final UserRepository userRepository;
	private final ReviewCommentRepository reviewCommentRepository;

	// 리뷰 등록
	public ResponseEntity<?> createReview(User user, ReviewRequestDto reviewRequestDto) {
		validateUserByEmail(user.getEmail());
		Ticket ticket = validateTicket(reviewRequestDto.getTicketId());
		Review review = Review.create(reviewRequestDto, ticket);
		reviewRepository.save(review);
		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);
	}

	private Ticket validateTicket(Long ticketId) {
		return ticketRepository.findById(ticketId).orElseThrow(
			() -> new CustomException(TICKET_NOT_FOUND));
	}

	private User validateUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(
			() -> new CustomException(UNAUTHORIZED_MEMBER));
	}

}
