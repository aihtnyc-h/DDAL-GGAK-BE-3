package com.ddalggak.finalproject.domain.auth;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.user.dto.EmailRequestDto;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.dto.UserRequestDto;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.error.ErrorResponse;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.mail.MailService;
import com.ddalggak.finalproject.global.mail.randomCode.RandomCodeDto;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final MailService mailService;
	private final AuthService authService;
	private final JwtUtil jwtUtil;

	@PostMapping("/email")
	public ResponseEntity<?> emailAuthentication(@Valid @RequestBody EmailRequestDto emailRequestDto,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<ObjectError> list = bindingResult.getAllErrors();
			for (ObjectError e : list) {
				System.out.println(e.getDefaultMessage());
			}
			return ErrorResponse.from(ErrorCode.INVALID_REQUEST);
		}
		mailService.sendRandomCode(emailRequestDto.getEmail());
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_SEND);
	}

	@GetMapping("/email")
	public ResponseEntity<?> emailAuthenticationWithRandomCode(@RequestBody RandomCodeDto randomCodeDto) {
		authService.emailAuthenticationWithRandomCode(randomCodeDto);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_AUTH);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody UserRequestDto userRequestDto,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<ObjectError> list = bindingResult.getAllErrors();
			for (ObjectError e : list) {
				System.out.println(e.getDefaultMessage());
			}
			return ErrorResponse.from(ErrorCode.INVALID_REQUEST);
		}

		authService.signup(userRequestDto);

		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);

	}

	@PostMapping("/login")
	public ResponseEntity<UserPageDto> login(@RequestBody UserRequestDto userRequestDto, HttpServletResponse response) {
		return authService.login(userRequestDto, response);
	}

	@PostMapping("/auth/logout")
	public ResponseEntity<SuccessResponseDto> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		String email = userDetails.getEmail();
		SecurityContextHolder.clearContext();
		jwtUtil.logout(email);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_LOGOUT);
	}
}
