package com.ddalggak.finalproject.domain.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.user.dto.EmailRequestDto;
import com.ddalggak.finalproject.domain.user.dto.NicknameDto;
import com.ddalggak.finalproject.domain.user.dto.ProfileDto;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.dto.UserRequestDto;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.service.UserService;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.error.ErrorResponse;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.jwt.token.TokenService.TokenService;
import com.ddalggak.finalproject.global.mail.MailService;
import com.ddalggak.finalproject.global.mail.randomCode.RandomCodeDto;
import com.ddalggak.finalproject.global.mail.randomCode.RandomCodeService;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final JwtUtil jwtUtil;
	private final TokenService tokenService;
	private final MailService mailService;
	private final RandomCodeService randomCodeService;

	@PostMapping("/auth/email")
	public ResponseEntity<?> emailAuthentication(@Valid @RequestBody EmailRequestDto emailRequestDto,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<ObjectError> list = bindingResult.getAllErrors();
			for (ObjectError e : list) {
				System.out.println(e.getDefaultMessage());
			}
			return ErrorResponse.from(ErrorCode.INVALID_REQUEST);
		}
		mailService.sendMail(emailRequestDto.getEmail());
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_SEND);

	}

	@GetMapping("/auth/email")
	public ResponseEntity<?> randomCoedAuthentication(@RequestBody RandomCodeDto randomCodeDto) {
		randomCodeService.authenticate(randomCodeDto);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_AUTH);
	}

	@PostMapping("/auth/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody UserRequestDto userRequestDto,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<ObjectError> list = bindingResult.getAllErrors();
			for (ObjectError e : list) {
				System.out.println(e.getDefaultMessage());
			}
			return ErrorResponse.from(ErrorCode.INVALID_REQUEST);
		}

		userService.signup(userRequestDto);

		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);

	}

	@PostMapping("/auth/login")
	public ResponseEntity<UserPageDto> login(@RequestBody UserRequestDto userRequestDto, HttpServletResponse response) {
		return userService.login(userRequestDto, response);
	}

	@PostMapping("/auth/logout")
	public ResponseEntity<?> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		String email = userDetails.getEmail();
		SecurityContextHolder.clearContext();
		jwtUtil.logout(email);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_LOGOUT);
	}

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
	public ResponseEntity<?> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return userService.getMyPage(userDetails.getEmail());
	}

	@GetMapping("/auth/reissue")
	public ResponseEntity<?> validateToken(HttpServletRequest request, HttpServletResponse response) {
		return tokenService.getAccessToken(request, response);
	}

	@GetMapping("/user/{userId}/Tickets")
	public ResponseEntity<?> getMyTickets(@PathVariable Long userId,
		TicketSearchCondition condition) {
		return userService.getMyTickets(userId, condition);
	}
}