package site.youtogether.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {

	// Room
	SINGLE_ROOM_PARTICIPATION_VIOLATION(HttpStatus.BAD_REQUEST, "하나의 방에만 참가할 수 있습니다"),
	USER_NOT_IN_ROOM(HttpStatus.BAD_REQUEST, "방에 참가 중인 사용자가 아닙니다"),
	ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 방입니다");

	private final HttpStatus status;
	private final String message;

	ErrorType(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

}
