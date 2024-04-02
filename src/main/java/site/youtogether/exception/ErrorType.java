package site.youtogether.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {

	// Room
	SINGLE_ROOM_PARTICIPATION_VIOLATION(HttpStatus.BAD_REQUEST, "하나의 방에만 참가할 수 있습니다"),
	ROOM_NO_EXISTENCE(HttpStatus.NOT_FOUND, "방이 존재하지 않습니다"),
	USER_NOT_IN_ROOM(HttpStatus.BAD_REQUEST, "방에 참여 중인 사용자가 아닙니다"),

	// User
	USER_NO_EXISTENCE(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"),

	// Cookie
	SESSION_COOKIE_NO_EXISTENCE(HttpStatus.NOT_FOUND, "세션 쿠키가 존재하지 않습니다");

	private final HttpStatus status;
	private final String message;

	ErrorType(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

}
