package site.youtogether.room.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomDetail {

	private String roomCode;
	private String roomTitle;
	private String videoTitle;
	private String videoThumbnail;
	private int capacity;
	private int currentParticipant;

	@Builder
	public RoomDetail(String roomCode, String roomTitle, String videoTitle, String videoThumbnail, int capacity, int currentParticipant) {
		this.roomCode = roomCode;
		this.roomTitle = roomTitle;
		this.videoTitle = videoTitle;
		this.videoThumbnail = videoThumbnail;
		this.capacity = capacity;
		this.currentParticipant = currentParticipant;
	}

}
