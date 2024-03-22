package site.youtogether.room.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RoomInfo {

	private final String name;
	private final int totalCapacity;

}
