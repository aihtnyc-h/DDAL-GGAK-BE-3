package com.ddalggak.finalproject.global.slackBot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.Slack;

@Configuration
public class SlackWebClientConfig {

	@Value("${slack.bot.token}")
	private String slackBotToken;

	@Bean
	public Slack slackWebClient() {
		return Slack.getInstance();
	}

	@Bean
	public String slackBotToken() {
		return slackBotToken;
	}
}
