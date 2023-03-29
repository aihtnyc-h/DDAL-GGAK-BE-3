package com.ddalggak.finalproject.domain.user.service;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.dto.UserRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.infra.aws.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final S3Uploader s3Uploader;

	private long fileSizeLimit = 10 * 1024 * 1024;//10메가바이트/킬로바이트/바이트

	@Transactional
	public void signup(UserRequestDto userRequestDto) {
		String email = userRequestDto.getEmail();

		Optional<User> foundUser = userRepository.findByEmail(email);

		if (foundUser.isPresent()) {
			throw new UserException(ErrorCode.DUPLICATE_EMAIL);
		}

		String nickname = "anonymous";
		String password = passwordEncoder.encode(userRequestDto.getPassword());

		User user = User.builder()
			.email(email)
			.nickname(nickname)
			.password(password)
			.build();

		userRepository.save(user);

	}

	@Transactional
	public ResponseEntity<UserPageDto> login(UserRequestDto userRequestDto, HttpServletResponse response) {
		String email = userRequestDto.getEmail();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("Invalid email or password"));
		String password = userRequestDto.getPassword();
		String dbPassword = user.getPassword();

		if (!passwordEncoder.matches(password, dbPassword)) {
			throw new RuntimeException("Invalid email or password");
		}

		response.addHeader(JwtUtil.AUTHORIZATION_HEADER,
			jwtUtil.login(email, user.getRole()));

		UserPageDto userPage = new UserPageDto(user);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(userPage);
	}

	@Transactional
	public void updateNickname(String nickname, String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.MEMBER_NOT_FOUND));

		User updatedUser = User.builder()
			.userId(user.getUserId())
			.email(user.getEmail())
			.password(user.getPassword())
			.nickname(nickname)
			.profile(user.getProfile())
			.build();
		userRepository.save(updatedUser);
	}

	@Transactional
	public void updateProfile(MultipartFile image, String email) throws IOException {
		fileSizeCheck(image);
		fileCheck(image);

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.MEMBER_NOT_FOUND));

		String storedFileName = s3Uploader.upload(image, "profile");

		User updatedUser = User.builder()
			.userId(user.getUserId())
			.email(user.getEmail())
			.password(user.getPassword())
			.nickname(user.getNickname())
			.profile(storedFileName)
			.build();
		userRepository.save(updatedUser);

	}

	private boolean fileCheck(MultipartFile file) {
		String fileName = StringUtils.getFilenameExtension(file.getOriginalFilename());

		if (fileName != null) {
			String exe = fileName.toLowerCase();
			if (exe.equals("jpg") || exe.equals("png") || exe.equals("jpeg") || exe.equals("webp")) {
				return false;
			}
		}
		return true;
	}

	private void fileSizeCheck(MultipartFile image) {
		long fileSize = image.getSize();

		if (fileSize > fileSizeLimit) {
			throw new IllegalArgumentException("총 용량 10MB이하만 업로드 가능합니다");
		}
	}

	@Transactional(readOnly = true)
	public ResponseEntity<?> getMyPage(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.MEMBER_NOT_FOUND));

		UserPageDto userPage = new UserPageDto(user);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(userPage);
	}
}
