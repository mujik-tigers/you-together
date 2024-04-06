package site.youtogether.room.dto;

import lombok.Getter;
import site.youtogether.room.Room;

@Getter
public class RoomListDetail {

	private String roomCode;
	private String roomTitle;
	private String videoTitle;
	private String videoThumbnail;
	private int capacity;
	private int currentParticipant;
	private boolean passwordExist;

	public RoomListDetail(Room room) {    // TODO: videoTitle, videoThumbnail 은 추후 결정
		this.roomCode = room.getCode();
		this.roomTitle = room.getTitle();
		this.capacity = room.getCapacity();
		this.currentParticipant = room.getParticipants().size();
		this.passwordExist = room.hasPassword();
		this.videoThumbnail = "https://i.ytimg.com/vi/sl7ih5rLfYM/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLDbjCXvhBJSBKs9bX_XMy_EfUtvSw";
		this.videoTitle = "궤도 '연애의 과학' 특강 1";
	}

}
