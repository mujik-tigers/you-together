package site.youtogether.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {

	// Cookie
	COOKIE_NO_EXISTENCE(HttpStatus.UNAUTHORIZED, "세션 쿠키가 없습니다"),
	COOKIE_INVALID(HttpStatus.BAD_REQUEST, "입력으로 들어온 세션 쿠키값과 대응되는 유저 아이디가 없습니다"),

	// User
	USER_NO_EXISTENCE(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"),
	SELF_ROLE_CHANGE(HttpStatus.FORBIDDEN, "자신의 역할을 변경할 수 없습니다"),
	HIGHER_OR_EQUAL_USER_ROLE_CHANGE(HttpStatus.FORBIDDEN, "자신보다 높거나 같은 사람의 역할은 변경할 수 없습니다"),
	HIGHER_OR_EQUAL_ROLE_CHANGE(HttpStatus.FORBIDDEN, "자신보다 높거나 같은 역할로 변경할 수 없습니다"),
	NOT_MANAGEABLE(HttpStatus.FORBIDDEN, "다른 사람의 역할을 변경할 수 없습니다"),
	CHAT_MESSAGE_SEND_DENIED(HttpStatus.FORBIDDEN, "채팅 메시지를 보낼 권한이 없습니다"),
	ROOM_TITLE_CHANGE_DENIED(HttpStatus.FORBIDDEN, "방 제목을 변경할 권한이 없습니다"),

	// Room
	ROOM_NO_EXISTENCE(HttpStatus.NOT_FOUND, "방이 없습니다"),
	SINGLE_ROOM_PARTICIPATION_VIOLATION(HttpStatus.BAD_REQUEST, "하나의 방에만 참가할 수 있습니다"),
	ROOM_PASSWORD_NOT_MATCH(HttpStatus.FORBIDDEN, "패스워드가 일치하지 않습니다"),
	ROOM_CAPACITY_EXCEEDED(HttpStatus.FORBIDDEN, "방의 참가 인원이 가득 찼습니다");

	private final HttpStatus status;
	private final String message;

	ErrorType(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

}
