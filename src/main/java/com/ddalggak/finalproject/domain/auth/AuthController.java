// package com.ddalggak.finalproject.domain.auth;
//
// import java.util.List;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import javax.validation.Valid;
//
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.validation.BindingResult;
// import org.springframework.validation.ObjectError;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.ddalggak.finalproject.domain.user.dto.EmailRequestDto;
// import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
// import com.ddalggak.finalproject.domain.user.dto.UserRequestDto;
// import com.ddalggak.finalproject.domain.user.exception.UserException;
// import com.ddalggak.finalproject.domain.user.repository.UserRepository;
// import com.ddalggak.finalproject.global.dto.SuccessCode;
// import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
// import com.ddalggak.finalproject.global.error.ErrorCode;
// import com.ddalggak.finalproject.global.error.ErrorResponse;
// import com.ddalggak.finalproject.global.jwt.JwtUtil;
// import com.ddalggak.finalproject.global.mail.MailService;
// import com.ddalggak.finalproject.global.mail.randomCode.RandomCodeDto;
// import com.ddalggak.finalproject.global.security.UserDetailsImpl;
//
// import io.jsonwebtoken.Claims;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;
//
// @Tag(name = "Auth Controller", description = "인증 관련 API 입니다.")
// @RestController
// @RequestMapping("/api/auth")
// @RequiredArgsConstructor
// public class AuthController {
// 	private final MailService mailService;
// 	private final AuthService authService;
// 	private final JwtUtil jwtUtil;
// 	private final UserRepository userRepository;
//
// 	@Operation(summary = "email authentication", description = "email 인증 코드 발송 post 메서드 체크")
// 	@PostMapping("/email")
// 	public ResponseEntity<?> emailAuthentication(
// 		@Valid @RequestBody EmailRequestDto emailRequestDto,
// 		BindingResult bindingResult) {
//
// 		if (bindingResult.hasErrors()) {
// 			List<ObjectError> list = bindingResult.getAllErrors();
// 			for (ObjectError e : list) {
// 				System.out.println(e.getDefaultMessage());
// 			}
// 			return ErrorResponse.from(ErrorCode.INVALID_REQUEST);
// 		}
// 		mailService.sendRandomCode(emailRequestDto.getEmail());
// 		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_SEND);
// 	}
//
// 	@Operation(summary = "email authentication check", description = "email 인증 코드 일치 확인 get 메서드 체크")
// 	@GetMapping("/email")
// 	public ResponseEntity<?> emailAuthenticationWithRandomCode(
// 		@RequestBody RandomCodeDto randomCodeDto) {
//
// 		authService.emailAuthenticationWithRandomCode(randomCodeDto);
// 		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_AUTH);
// 	}
//
// 	@Operation(summary = "signup", description = "회원가입 post 메서드 체크")
// 	@PostMapping("/signup")
// 	public ResponseEntity<?> signup(
// 		@Valid @RequestBody UserRequestDto userRequestDto,
// 		BindingResult bindingResult) {
//
// 		if (bindingResult.hasErrors()) {
// 			List<ObjectError> list = bindingResult.getAllErrors();
// 			for (ObjectError e : list) {
// 				System.out.println(e.getDefaultMessage());
// 			}
// 			return ErrorResponse.from(ErrorCode.INVALID_REQUEST);
// 		}
//
// 		authService.signup(userRequestDto);
//
// 		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);
//
// 	}
//
// 	@Operation(summary = "login", description = "로그인 post 메서드 체크")
// 	@PostMapping("/login")
// 	public ResponseEntity<UserPageDto> login(
// 		@RequestBody UserRequestDto userRequestDto,
// 		HttpServletResponse response) {
//
// 		return authService.login(userRequestDto, response);
// 	}
//
// 	@Operation(summary = "logout", description = "로그아웃 post 메서드 체크")
// 	@PostMapping("/logout")
// 	public ResponseEntity<SuccessResponseDto> logout(
// 		@AuthenticationPrincipal UserDetailsImpl userDetails) {
//
// 		String email = userDetails.getEmail();
// 		SecurityContextHolder.clearContext();
// 		jwtUtil.logout(email);
// 		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_LOGOUT);
// 	}
//
// 	@Operation(summary = "validate token", description = "권한 확인 get 메서드 체크")
// 	@GetMapping("/validToken")
// 	public ResponseEntity<?> validateToken(
// 		HttpServletRequest request) {
//
// 		String token = jwtUtil.resolveToken(request);
// 		Claims claims;
// 		if (token != null) {
// 			if (jwtUtil.validateToken(token)) {
// 				claims = jwtUtil.getUserInfo(token);
// 			} else {
// 				return ErrorResponse.from(ErrorCode.INVALID_REQUEST);
// 			}
// 			userRepository.findByEmail(claims.getSubject())
// 				.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
// 		} else
// 			throw new UserException(ErrorCode.INVALID_REQUEST);
// 		return SuccessResponseDto.of(SuccessCode.SUCCESS_AUTH);
// 	}
// }
