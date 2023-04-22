package com.ddalggak.finalproject.global.slackBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlarmController {
	private AlarmService alarmService;

	@PostMapping("/alarm")
	public ResponseEntity<String> createAlarm(@RequestBody ChannelIdDto channelIdDto) {
		alarmService.createAlarm(channelIdDto.getChannelId(), channelIdDto.getAlarmTime());
		return new ResponseEntity<>("Alarm created", HttpStatus.CREATED);
	}

	@PostMapping("/api/slack/channel_id")
	public ResponseEntity<String> setChannelId(@RequestBody ChannelIdDto channelIdDto) {
		return new ResponseEntity<>("Channel ID set", HttpStatus.OK);
	}
}
//POST /alarm?channelId=C12345678&alarmTime=14:30