package com.ddalggak.finalproject.global.nginx;

import java.util.Arrays;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NginxController {
	private final Environment env;

	//현재 어떤 포트로 돌아가는지 확인용. set1 = 8081, set2 = 8082
	@GetMapping("/nginx")
	public String profile() {
		//현재 동작중인 프로파일의 이름을 반환
		return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("nginx 테스트1");
	}

	//무중단배포 테스트를 위한 version 확인용. 새로고침하면 return 값이 자동으로 바뀌는지
	@GetMapping("/version")
	public String checkVersion() {
		return "ver2";
	}

}

