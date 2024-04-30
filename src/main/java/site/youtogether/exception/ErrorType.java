package site.youtogether.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {

	// Jwt
	AUTHORIZATION_HEADER_NO_EXISTENCE(HttpStatus.UNAUTHORIZED, "인증 헤더가 없습니다"),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),

	// User
	USER_NO_EXISTENCE(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"),
	SELF_ROLE_CHANGE(HttpStatus.FORBIDDEN, "자신의 역할을 변경할 수 없습니다"),
	HIGHER_OR_EQUAL_USER_ROLE_CHANGE(HttpStatus.FORBIDDEN, "자신보다 높거나 같은 사람의 역할은 변경할 수 없습니다"),
	HIGHER_OR_EQUAL_ROLE_CHANGE(HttpStatus.FORBIDDEN, "자신보다 높거나 같은 역할로 변경할 수 없습니다"),
	NOT_MANAGEABLE(HttpStatus.FORBIDDEN, "다른 사람의 역할을 변경할 수 없습니다"),
	CHAT_MESSAGE_SEND_DENIED(HttpStatus.FORBIDDEN, "채팅 메시지를 보낼 권한이 없습니다"),
	ROOM_TITLE_CHANGE_DENIED(HttpStatus.FORBIDDEN, "방 제목을 변경할 권한이 없습니다"),
	USERS_IN_DIFFERENT_ROOM(HttpStatus.BAD_REQUEST, "해당 방에 두 유저가 존재하지 않습니다"),
	VIDEO_EDIT_DENIED(HttpStatus.FORBIDDEN, "영상 관련 작업을 할 권한이 없습니다"),

	// Room
	ROOM_NO_EXISTENCE(HttpStatus.NOT_FOUND, "방이 없습니다"),
	SINGLE_ROOM_PARTICIPATION_VIOLATION(HttpStatus.BAD_REQUEST, "하나의 방에만 참가할 수 있습니다"),
	ROOM_PASSWORD_NOT_MATCH(HttpStatus.FORBIDDEN, "패스워드가 일치하지 않습니다"),
	ROOM_CAPACITY_EXCEEDED(HttpStatus.FORBIDDEN, "방의 참가 인원이 가득 찼습니다"),
	USER_ABSENT(HttpStatus.BAD_REQUEST, "방 안에 사용자가 존재하지 않습니다"),

	// Cookie
	COOKIE_NO_EXISTENCE(HttpStatus.UNAUTHORIZED, "쿠키가 없습니다"),

	// Playlist
	PLAYLIST_NO_EXISTENCE(HttpStatus.NOT_FOUND, "플레이리스트가 없습니다"),
	PLAYLIST_EMPTY(HttpStatus.NOT_FOUND, "플레이리스트에 재생할 영상이 없습니다"),
	INVALID_VIDEO_ORDER(HttpStatus.BAD_REQUEST, "변경하고자 하는 순서가 유효하지 않습니다"),
	PLAYLIST_INDEX_OUT_OF_BOUNDS(HttpStatus.BAD_REQUEST, "플레이리스트를 초과하는 인덱스입니다"),
	PLAYING_VIDEO_NO_EXISTENCE(HttpStatus.NOT_FOUND, "현재 재생중인 영상이 없습니다"),
	INVALID_VIDEO_RATE(HttpStatus.BAD_REQUEST, "유효하지 않은 재생속도입니다");

	private final HttpStatus status;
	private final String message;

	ErrorType(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

}
