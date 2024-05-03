package site.youtogether.room.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NewRoom {

	private final String roomCode;
	private final String password;

}
