package site.youtogether.room.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.youtogether.playlist.PlayingVideo;
import site.youtogether.room.Participant;
import site.youtogether.room.Room;
import site.youtogether.user.User;

@AllArgsConstructor
@Getter
public class RoomDetail {

	private final String roomCode;
	private final String roomTitle;
	private final Participant user;
	private final int capacity;
	private final int currentParticipant;
	private final boolean passwordExist;
	private String currentVideoId;
	private String currentVideoTitle;
	private String currentChannelTitle;
	private double currentVideoTime;

	public RoomDetail(Room room, User user, Optional<PlayingVideo> playingVideo) {
		this.roomCode = room.getCode();
		this.roomTitle = room.getTitle();
		this.user = new Participant(user);
		this.capacity = room.getCapacity();
		this.currentParticipant = room.getParticipantCount();
		this.passwordExist = room.getPassword() != null;
		playingVideo.ifPresent((p) -> {
			this.currentVideoId = p.getVideoId();
			this.currentVideoTitle = p.getVideoTitle();
			this.currentChannelTitle = p.getChannelTitle();
			this.currentVideoTime = p.getCurrentTime();
		});
	}

}
