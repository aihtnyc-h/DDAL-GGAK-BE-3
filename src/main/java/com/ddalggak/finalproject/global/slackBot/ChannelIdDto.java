package com.ddalggak.finalproject.global.slackBot;

public class ChannelIdDto {
	private String channelId;
	private String alarmTime;

	public ChannelIdDto() {
	}

	public ChannelIdDto(String channelId, String alarmTime) {
		this.channelId = channelId;
		this.alarmTime = alarmTime;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
}
