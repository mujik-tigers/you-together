package site.youtogether.message.application;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.ParticipantsInfo;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {

	private final RoomStorage roomStorage;
	private final SimpMessageSendingOperations messagingTemplate;
	private final ObjectMapper objectMapper;

	// when redis message publish, onMessage of the subscriber receive and process the message
	public void sendChatMessage(String publishMessage) {
		try {
			ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
			messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomCode(), chatMessage);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendParticipantsInfo(String roomCode) {
		ParticipantsInfo participantsInfo = roomStorage.findById(roomCode)
			.map(Room::getParticipants)
			.map(ParticipantsInfo::new)
			.orElseThrow(RoomNoExistenceException::new);

		messagingTemplate.convertAndSend("/sub/chat/room/" + roomCode, participantsInfo);
	}

}
