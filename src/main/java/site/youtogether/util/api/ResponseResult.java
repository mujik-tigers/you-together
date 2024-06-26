package site.youtogether.util.api;

import lombok.Getter;

@Getter
public enum ResponseResult {

	// Common
	EXCEPTION_OCCURRED("예외가 발생했습니다"),

	// Room
	ROOM_CREATION_SUCCESS("방 생성에 성공했습니다"),
	ROOM_ENTER_SUCCESS("방 입장에 성공했습니다"),
	ROOM_LIST_FETCH_SUCCESS("방 목록 조회에 성공했습니다"),
	ROOM_TITLE_CHANGE_SUCCESS("방 제목 변경에 성공했습니다"),

	// User
	USER_NICKNAME_CHANGE_SUCCESS("유저 닉네임 변경에 성공했습니다"),
	USER_ROLE_CHANGE_SUCCESS("유저의 역할 변경에 성공했습니다"),
	USER_NICKNAME_DUPLICATION_CHECK_SUCCESS("닉네임 중복 검사에 성공했습니다"),

	// Playlist
	PLAYLIST_ADD_SUCCESS("플레이리스트에 비디오 추가를 성공했습니다"),
	PLAYLIST_DELETE_SUCCESS("플레이리스트의 비디오 삭제를 성공했습니다"),
	PLAYLIST_REORDER_SUCCESS("플레이리스트의 비디오 순서 변경에 성공했습니다"),
	PLAY_NEXT_VIDEO_SUCCESS("플레이리스트의 다음 비디오 재생에 성공했습니다");

	private final String description;

	ResponseResult(String description) {
		this.description = description;
	}

}
