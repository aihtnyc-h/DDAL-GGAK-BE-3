package com.ddalggak.finalproject.domain.project.projectInviteCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RedisHash(value = "projectInviteCode", timeToLive = 60 * 60 * 24 * 1000L)
public class ProjectInviteCode {
	@Id
	private String projectInviteCode;
	private String projectId;
}
