package com.ddalggak.finalproject.global.nginx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebRestControllerTest {

	@InjectMocks
	private WebRestController webRestController;

	@Mock
	private Environment env;

	@BeforeEach
	public void setUp() {
	}

	@Test
	public void getProfile() {
		// 준비
		String[] profiles = {"test-set1", "test-set2"};
		when(env.getActiveProfiles()).thenReturn(profiles);

		// 실행
		String result = webRestController.getProfile();

		// 확인
		assertEquals("test-set1", result);
	}

	@Test
	public void getProfile_noSetProfile() {
		// 준비
		String[] profiles = {"test1", "test2"};
		when(env.getActiveProfiles()).thenReturn(profiles);

		// 실행
		String result = webRestController.getProfile();

		// 확인
		assertEquals("", result);
	}
}
