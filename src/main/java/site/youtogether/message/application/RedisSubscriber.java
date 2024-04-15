package site.youtogether.message.application;

import java.util.List;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.ParticipantsInfoMessage;
import site.youtogether.message.RoomTitleMessage;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.dto.UserInfo;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {

	private final RoomStorage roomStorage;
	private final ObjectMapper objectMapper;
	private final SimpMessageSendingOperations messagingTemplate;

	public void sendChat(String publishMessage) {
		try {
			ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
			messagingTemplate.convertAndSend("/sub/messages/rooms/" + chatMessage.getRoomCode(), chatMessage);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendParticipantsInfo(String roomCode) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		List<UserInfo> participants = room.getParticipants().values().stream()
			.map(UserInfo::new)
			.toList();

		ParticipantsInfoMessage participantsInfoMessage = new ParticipantsInfoMessage(participants);
		messagingTemplate.convertAndSend("/sub/messages/rooms/" + roomCode, participantsInfoMessage);
	}

	public void sendRoomTitle(String roomCode) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		RoomTitleMessage roomTitleMessage = new RoomTitleMessage(room);
		messagingTemplate.convertAndSend("/sub/messages/rooms/" + roomCode, roomTitleMessage);
	}

}
