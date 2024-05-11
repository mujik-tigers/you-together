package site.youtogether.room.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.youtogether.message.ChatMessage;
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
	private final List<ChatMessage> chatHistory;

	public RoomDetail(Room room, User user, List<ChatMessage> chatHistory) {
		this.roomCode = room.getCode();
		this.roomTitle = room.getTitle();
		this.user = new Participant(user);
		this.capacity = room.getCapacity();
		this.currentParticipant = room.getParticipantCount();
		this.passwordExist = room.getPassword() != null;
		this.chatHistory = chatHistory;
	}

}
