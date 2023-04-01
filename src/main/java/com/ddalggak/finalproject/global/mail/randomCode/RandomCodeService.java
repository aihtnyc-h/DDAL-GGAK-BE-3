package com.ddalggak.finalproject.global.mail.randomCode;

import org.springframework.stereotype.Service;

import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RandomCodeService {
	private final RandomCodeRepository randomCodeRepository;

	public void authenticate(RandomCodeDto randomCodeDto) {
		String email = randomCodeDto.getEmail();
		String randomCode = randomCodeDto.getRandomCode();

		RandomCode user = randomCodeRepository.findById(email)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_RANDOM_CODE));
		
		if (randomCode.equals(user.getRandomCode()) && email.equals(user.getEmail())) {
			randomCodeRepository.delete(user);
		}
	}
}
