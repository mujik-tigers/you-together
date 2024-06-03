package site.youtogether.room.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.youtogether.playlist.PlayingVideo;
import site.youtogether.room.Room;

@AllArgsConstructor
@Getter
public class RoomListDetail {

	private String roomCode;
	private String roomTitle;
	private String videoTitle;
	private String videoThumbnail;
	private int capacity;
	private int currentParticipant;
	private boolean passwordExist;

	public RoomListDetail(Room room, Optional<PlayingVideo> playingVideo) {
		this.roomCode = room.getCode();
		this.roomTitle = room.getTitle();
		this.capacity = room.getCapacity();
		this.currentParticipant = room.getParticipantCount();
		this.passwordExist = room.hasPassword();
		playingVideo.ifPresent((p) -> {
			this.videoThumbnail = p.getThumbnail();
			this.videoTitle = p.getVideoTitle();
		});
	}

}
