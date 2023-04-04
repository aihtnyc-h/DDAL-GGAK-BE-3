package com.ddalggak.finalproject.global.error;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	/* 400 BAD_REQUEST : 잘못된 요청  */
	INVALID_REFRESH_TOKEN(BAD_REQUEST, "A-001", "Please enter valid refresh token."),
	MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "A-002", "Please enter valid refresh token user info."),
	INVALID_REQUEST(BAD_REQUEST, "A-003", "Invalid request."),
	INVALID_PASSWORD(BAD_REQUEST, "A-004", "Please enter valid password."),
	INVALID_RANDOM_CODE(BAD_REQUEST, "A-005", "Please enter valid authentication code."),
	INVALID_EMAIL(BAD_REQUEST, "A-006", "Please enter valid email."),

	/* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
	INVALID_AUTH_TOKEN(UNAUTHORIZED, "B-001", "Please enter an authorized token. "),
	UNAUTHORIZED_MEMBER(UNAUTHORIZED, "B-002", "Please Log in"),

	/* 403 FORBIDDEN : 권한 없음*/
	UNAUTHENTICATED_USER(FORBIDDEN, "C-001", "You do not have access."),

	/* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
	MEMBER_NOT_FOUND(NOT_FOUND, "D-001", "Unable to find user info"),
	EMPTY_CLIENT(NOT_FOUND, "D-002", "There is no valid user."),
	PHOTO_NOT_FOUND(NOT_FOUND, "D-003", "The photo could not be found."),
	COMMENT_NOT_FOUND(NOT_FOUND, "D-004", "The comment could not be found."),
	TICKET_NOT_FOUND(NOT_FOUND, "D-005", "The ticket could not be found."),
	PROJECT_NOT_FOUND(NOT_FOUND, "D-006", "The project could not be found."),
	TASK_NOT_FOUND(NOT_FOUND, "D-007", "The task could not be found."),
	LABEL_NOT_FOUND(NOT_FOUND, "D-008", "The label could not be found"),
	REVIEW_NOT_FOUND(NOT_FOUND, "D-009", "The review could not be found"),
	REVIEW_COMMENT_NOT_FOUND(NOT_FOUND, "D-010", "The review comment could not be found"),

	/* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
	DUPLICATE_RESOURCE(CONFLICT, "E-001", "Data already exists."),
	DUPLICATE_MEMBER(CONFLICT, "E-002", "Duplicate user exists."),
	DUPLICATE_EMAIL(CONFLICT, "E-003", "Email address is already in use. Please try a different email address."),

	/* 415 UNSUPPORTED_MEDIA_TYPE : 지원하지 않는 미디어 타입 */
	TYPE_MISMATCH(UNSUPPORTED_MEDIA_TYPE, "F-001", "Unsupported media type."),

	/* 500 INTERNAL_SERVER_ERROR : 서버에서 문제 일어남 */
	SERVER_ERROR(INTERNAL_SERVER_ERROR, "F-001", "Please check your input value.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;
}

