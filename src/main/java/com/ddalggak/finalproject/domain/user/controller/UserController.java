package com.ddalggak.finalproject.domain.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.user.dto.NicknameDto;
import com.ddalggak.finalproject.domain.user.dto.ProfileDto;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.domain.user.service.UserService;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.error.ErrorResponse;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@PutMapping("/user/nickname")
	public NicknameDto updateNickname(@Valid @RequestBody NicknameDto nicknameDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<ObjectError> list = bindingResult.getAllErrors();
			for (ObjectError e : list) {
				System.out.println(e.getDefaultMessage());
			}
			throw new UserException(ErrorCode.INVALID_REQUEST);
		}
		return userService.updateNickname(nicknameDto.getNickname(), userDetails.getEmail());
	}

	@PutMapping("/user/profile")
	public ProfileDto updateProfile(@RequestPart(value = "image") MultipartFile image,
		@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		return userService.updateProfile(image, userDetails.getEmail());
	}

	@GetMapping("/user")
	public ResponseEntity<UserPageDto> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return userService.getMyPage(userDetails.getEmail());
	}

	@GetMapping("/user/{userId}/Tickets")
	public ResponseEntity<?> getMyTickets(@PathVariable Long userId,
		TicketSearchCondition condition) {
		return userService.getMyTickets(userId, condition);
	}

	@GetMapping("/auth/validToken")
	public ResponseEntity<?> validateToken(HttpServletRequest request, HttpServletResponse response) {
		String token = jwtUtil.resolveToken(request);
		Claims claims;
		if (token != null) {
			if (jwtUtil.validateToken(token)) {
				claims = jwtUtil.getUserInfo(token);
			} else {
				return ErrorResponse.from(ErrorCode.INVALID_REQUEST);
			}
			userRepository.findByEmail(claims.getSubject())
				.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
		} else
			throw new UserException(ErrorCode.INVALID_REQUEST);
		return SuccessResponseDto.of(SuccessCode.SUCCESS_AUTH);
	}
}