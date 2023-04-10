package com.ddalggak.finalproject.domain.user.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.user.dto.NicknameDto;
import com.ddalggak.finalproject.domain.user.dto.ProfileDto;
import com.ddalggak.finalproject.domain.user.dto.UserMapper;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.infra.aws.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserMapper userMapper;
	private final UserRepository userRepository;
	private final S3Uploader s3Uploader;

	@Value("${file.size.limit}")
	private Long fileSizeLimit;//10메가바이트/킬로바이트/바이트

	@Transactional
	public NicknameDto updateNickname(String nickname, String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.MEMBER_NOT_FOUND));

		user.updateNickname(nickname);
		return new NicknameDto(user.getNickname());
	}

	@Transactional
	public ProfileDto updateProfile(MultipartFile image, String email) throws IOException {
		fileSizeCheck(image);
		fileCheck(image);

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.MEMBER_NOT_FOUND));

		String storedFileName = s3Uploader.upload(image, "profile");

		user.updateProfile(storedFileName);

		return new ProfileDto(storedFileName);
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
	public ResponseEntity<UserPageDto> getMyPage(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserException(ErrorCode.MEMBER_NOT_FOUND));

		UserPageDto userPageDto = userMapper.toUserPageDto(user);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(userPageDto);
	}

	public ResponseEntity<?> getMyTickets(Long userId, TicketSearchCondition condition) {
		User user = userRepository.findById(userId).orElseThrow(
			() -> new UserException(ErrorCode.MEMBER_NOT_FOUND)
		);
		List<DateTicket> completedTicketCountByDate = userRepository.getCompletedTicketCountByDate(condition, userId);
		return ResponseEntity.ok(completedTicketCountByDate);
	}
}
