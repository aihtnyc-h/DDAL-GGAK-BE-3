package com.ddalggak.finalproject.global.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
	//todo 상세코드 작성

	/*200 OK : 에러 없이 전송 성공*/
	IMAGE_SUCCESSFULLY_DELETED(HttpStatus.OK, "succesfully deleted"),
	SUCCESS_LOGIN(HttpStatus.OK, "login success"),
	SUCCESS_LOGOUT(HttpStatus.OK, "logout success"),
	SUCCESS_UPLOAD(HttpStatus.OK, "upload success"),
	SUCCESS_SEND(HttpStatus.OK, "send success"),
	DELETED_SUCCESSFULLY(HttpStatus.OK, "successfully deleted"),
	GET_ACCESS_TOKEN(HttpStatus.OK, "get access token success"),
	UPDATED_SUCCESSFULLY(HttpStatus.OK, "successfully updated"),

	/*201 CREATED : REQUEST COMPLETE, RESOURCE SUCCESSFULLY CREATED*/
	CREATED_SUCCESSFULLY(HttpStatus.CREATED, "successfully created"),
	JOINED_SUCCESSFULLY(HttpStatus.CREATED, "successfully joined"),
	;

	private final HttpStatus httpStatus;
	private final String detail;
}
