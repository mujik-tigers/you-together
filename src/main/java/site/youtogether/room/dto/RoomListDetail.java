package site.youtogether.room.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomListDetail {

	private String roomCode;
	private String roomTitle;
	private String videoTitle;
	private String videoThumbnail;
	private int capacity;
	private int currentParticipant;
	private boolean passwordExist;

	@Builder
	public RoomListDetail(String roomCode, String roomTitle, String videoTitle, String videoThumbnail, int capacity, int currentParticipant,
		boolean passwordExist) {
		this.roomCode = roomCode;
		this.roomTitle = roomTitle;
		this.videoTitle = videoTitle;
		this.videoThumbnail = videoThumbnail;
		this.capacity = capacity;
		this.currentParticipant = currentParticipant;
		this.passwordExist = passwordExist;
	}

}
