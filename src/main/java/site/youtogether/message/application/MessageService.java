package site.youtogether.message.application;

import static site.youtogether.util.AppConstants.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.playlist.PlaylistNoExistenceException;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.message.AlarmMessage;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.ParticipantsMessage;
import site.youtogether.message.PlaylistMessage;
import site.youtogether.message.RoomTitleMessage;
import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.dto.VideoInfo;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.room.Participant;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.infrastructure.UserStorage;

@Service
@RequiredArgsConstructor
public class MessageService {

	private final RoomStorage roomStorage;
	private final UserStorage userStorage;
	private final PlaylistStorage playlistStorage;
	private final SimpMessageSendingOperations messagingTemplate;
	private final RedisTemplate<String, ChatMessage> chatRedisTemplate;

	public void sendChat(ChatMessage chatMessage) {
		messagingTemplate.convertAndSend(SUBSCRIBE_PATH + chatMessage.getRoomCode(), chatMessage);

		chatRedisTemplate.opsForList().rightPush(CHAT_PREFIX + chatMessage.getRoomCode(), chatMessage);
		chatRedisTemplate.opsForList().trim(CHAT_PREFIX + chatMessage.getRoomCode(), -100, -1);
	}

	public void sendParticipants(String roomCode) {
		if (!roomStorage.existsById(roomCode)) {
			throw new RoomNoExistenceException();
		}

		List<Participant> participants = userStorage.findAllByCurrentRoomCode(roomCode).stream()
			.map(Participant::new)
			.toList();

		ParticipantsMessage participantsMessage = new ParticipantsMessage(participants);
		messagingTemplate.convertAndSend(SUBSCRIBE_PATH + roomCode, participantsMessage);
	}

	public void sendRoomTitle(String roomCode) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		RoomTitleMessage roomTitleMessage = new RoomTitleMessage(room);
		messagingTemplate.convertAndSend(SUBSCRIBE_PATH + roomCode, roomTitleMessage);
	}

	public void sendPlaylist(String roomCode) {
		Playlist playlist = playlistStorage.findById(roomCode)
			.orElseThrow(PlaylistNoExistenceException::new);

		AtomicInteger index = new AtomicInteger(0);
		List<VideoInfo> videos = playlist.getVideos().stream()
			.map(v -> new VideoInfo(index.getAndIncrement(), v))
			.toList();

		PlaylistMessage playlistMessage = new PlaylistMessage(videos);
		messagingTemplate.convertAndSend(SUBSCRIBE_PATH + roomCode, playlistMessage);
	}

	public void sendVideoSyncInfo(VideoSyncInfoMessage message) {
		messagingTemplate.convertAndSend(SUBSCRIBE_PATH + message.getRoomCode(), message);
	}

	public void sendAlarm(AlarmMessage message) {
		messagingTemplate.convertAndSend(SUBSCRIBE_PATH + message.getRoomCode(), message);
	}

}
