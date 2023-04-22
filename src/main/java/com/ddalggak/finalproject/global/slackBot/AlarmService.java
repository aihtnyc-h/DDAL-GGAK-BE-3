package com.ddalggak.finalproject.global.slackBot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.users.UsersLookupByEmailResponse;
import com.slack.api.Slack;

@Service
public class AlarmService {

	@Value("${slack.bot.token}")
	private String slackBotToken;

	@Autowired
	private Slack slack;

	private final Logger logger = LoggerFactory.getLogger(AlarmService.class);

	@PostConstruct
	public void init() {
		slack = Slack.getInstance();
	}

	public void sendAlarmMessage(String channelId, String text) {
		try {
			ChatPostMessageResponse response = slack.methods(slackBotToken).chatPostMessage(req -> req.channel(channelId).text(text));
			if (!response.isOk()) {
				logger.error("Error sending message: {}", response.getError());
			}
		} catch (Exception e) {
			logger.error("Exception while sending alarm message", e);
		}
	}

	public void createAlarm(String channelId, String alarmTime) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				sendAlarmMessage(channelId, "⏰ 업무를 완료했습니다!");
			}
		};
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			Date alarmDate = dateFormat.parse(alarmTime);
			timer.schedule(task, alarmDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	public String getUserIdByEmail(String email) {
		try {
			UsersLookupByEmailResponse response = slack.methods(slackBotToken).usersLookupByEmail(req -> req.email(email));
			if (response.isOk()) {
				return response.getUser().getId();
			} else {
				logger.error("Error looking up user by email: {}", response.getError());
			}
		} catch (Exception e) {
			logger.error("Exception while looking up user by email", e);
		}
		return null;
	}
}


