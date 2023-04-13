package com.ddalggak.finalproject.domain.user.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.user.dto.NicknameDto;
import com.ddalggak.finalproject.domain.user.dto.ProfileDto;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.service.UserService;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "User Controller", description = "유저 관련 API 입니다.")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@Operation(summary = "update nickname", description = "nickname 수정 put 메서드 체크")
	@PutMapping("/nickname")
	public NicknameDto updateNickname(
		@Valid @RequestBody NicknameDto nicknameDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			List<ObjectError> list = bindingResult.getAllErrors();
			for (ObjectError e : list) {
				System.out.println(e.getDefaultMessage());
			}
			throw new UserException(ErrorCode.INVALID_REQUEST);
		}
		return userService.updateNickname(nicknameDto.getNickname(), userDetails.getEmail());
	}

	@Operation(summary = "update profile", description = "profile 수정 put 메서드 체크")
	@PutMapping("/profile")
	public ProfileDto updateProfile(
		@RequestPart(value = "image") MultipartFile image,
		@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

		return userService.updateProfile(image, userDetails.getEmail());
	}

	@Operation(summary = "get user page", description = "user page 찾기 get 메서드 체크")
	@GetMapping
	public ResponseEntity<UserPageDto> getMyPage(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		return userService.getMyPage(userDetails.getEmail());
	}

	@Operation(summary = "get user's completed ticket All", description = "유저의 모든 티켓 확인")
	@GetMapping("/{userId}/completedTickets")
	public ResponseEntity<List<DateTicket>> getMyCompletedTickets(
		@PathVariable Long userId,
		TicketSearchCondition condition) {
		return userService.getMyCompletedTickets(userId, condition);
	}

	@Operation(summary = "get user ticket page", description = "user ticket page 찾기 get 메서드 체크")
	@GetMapping("/{userId}/Tickets")
	public ResponseEntity<Slice<TicketResponseDto>> getMyTickets(
		@PathVariable Long userId,
		TicketSearchCondition condition,
		@PageableDefault(size = 100) Pageable pageable) {
		return userService.getMyTickets(userId, pageable, condition);
	}
}