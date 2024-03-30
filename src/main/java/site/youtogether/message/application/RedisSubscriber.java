package site.youtogether.message.application;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.ParticipantInfo;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {

	private final RoomStorage roomStorage;
	private final ObjectMapper objectMapper;
	private final SimpMessageSendingOperations messagingTemplate;

	// when redis message publish, onMessage of the subscriber receive and process the message
	public void sendMessage(String publishMessage) {
		try {
			ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
			messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendParticipantsInfo(String roomId) {
		ParticipantInfo participantInfo = roomStorage.findById(roomId)
			.map(Room::getParticipants)
			.map(ParticipantInfo::new)
			.orElseThrow(RoomNoExistenceException::new);

		messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, participantInfo);
	}

}
