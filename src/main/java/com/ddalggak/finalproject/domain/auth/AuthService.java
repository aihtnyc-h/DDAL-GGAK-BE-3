package com.ddalggak.finalproject.domain.auth;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.user.dto.UserMapper;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.dto.UserRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.domain.user.role.UserRole;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.mail.randomCode.RandomCode;
import com.ddalggak.finalproject.global.mail.randomCode.RandomCodeDto;
import com.ddalggak.finalproject.global.mail.randomCode.RandomCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final UserMapper userMapper;
	private final RandomCodeRepository randomCodeRepository;

	@Transactional
	public void signup(UserRequestDto userRequestDto) {
		String email = userRequestDto.getEmail();

		Optional<User> foundUser = userRepository.findByEmail(email);

		if (foundUser.isPresent()) {
			throw new UserException(ErrorCode.DUPLICATE_MEMBER);
		}
		String[] parts = email.split("@");
		String nickname = parts[0];
		String password = passwordEncoder.encode(userRequestDto.getPassword());

		User user = User.builder()
			.email(email)
			.nickname(nickname)
			.password(password)
			.role(UserRole.USER)
			.build();

		userRepository.save(user);

	}

	@Transactional
	public ResponseEntity<UserPageDto> login(UserRequestDto userRequestDto, HttpServletResponse response) {
		String email = userRequestDto.getEmail();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_EMAIL_PASSWORD));
		String password = userRequestDto.getPassword();
		String dbPassword = user.getPassword();

		if (!passwordEncoder.matches(password, dbPassword)) {
			throw new UserException(ErrorCode.INVALID_EMAIL_PASSWORD);
		}

		String accessToken = jwtUtil.login(email, user.getRole());
		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);

		UserPageDto userPageDto = userMapper.toUserPageDto(user);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(userPageDto);
	}

	@Transactional
	public void emailAuthenticationWithRandomCode(RandomCodeDto randomCodeDto) {
		String email = randomCodeDto.getEmail();
		String randomCode = randomCodeDto.getRandomCode();

		RandomCode user = randomCodeRepository.findById(email)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_RANDOM_CODE));

		if (randomCode.equals(user.getRandomCode()) && email.equals(user.getEmail())) {
			randomCodeRepository.delete(user);
		}
	}
}
