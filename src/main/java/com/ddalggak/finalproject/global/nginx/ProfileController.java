package com.ddalggak.finalproject.global.nginx;

import java.util.Arrays;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {

	private final Environment env;

	// 현재 어떤 포트로 돌아가는지 확인용. set1 = 8081, set2 = 8082
	@GetMapping("/profile")
	public String profile() {
		// 현재 동작중인 프로파일의 이름을 반환
		return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("");
	}

	// 무중단배포 테스트를 위한 version 확인용. 새로고침하면 return 값이 자동으로 바뀌는지
	@GetMapping("/version")
	public String checkVersion() {
		return "ver2";
	}
}
//
// import java.util.Arrays;
// import java.util.List;
//
// import org.springframework.core.env.Environment;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// @RestController
// public class ProfileController {
// 	private final Environment env;
//
// 	@GetMapping("/profile")
// 	public String profile() {
// 		List<String> profiles = Arrays.asList(env.getActiveProfiles());
//
// 		List<String> realProfiles = Arrays.asList("real", "real1", "real2");
//
// 		String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(0);
//
// 		return profiles.stream()
// 			.filter(realProfiles::contains)
// 			.findAny()
// 			.orElse(defaultProfile);
// 	}
//
// }
