package site.youtogether.message.application;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.playlist.PlaylistNoExistenceException;
import site.youtogether.exception.room.RoomNoExistenceException;
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

@Service
@RequiredArgsConstructor
public class MessageService {

	private final RoomStorage roomStorage;
	private final PlaylistStorage playlistStorage;
	private final SimpMessageSendingOperations messagingTemplate;

	public void sendChat(ChatMessage chatMessage) {
		messagingTemplate.convertAndSend("/sub/messages/rooms/" + chatMessage.getRoomCode(), chatMessage);
	}

	public void sendParticipants(String roomCode) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		List<Participant> participants = room.getParticipants().values().stream().toList();

		ParticipantsMessage participantsMessage = new ParticipantsMessage(participants);
		messagingTemplate.convertAndSend("/sub/messages/rooms/" + roomCode, participantsMessage);
	}

	public void sendRoomTitle(String roomCode) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		RoomTitleMessage roomTitleMessage = new RoomTitleMessage(room);
		messagingTemplate.convertAndSend("/sub/messages/rooms/" + roomCode, roomTitleMessage);
	}

	public void sendPlaylist(String roomCode) {
		Playlist playlist = playlistStorage.findById(roomCode)
			.orElseThrow(PlaylistNoExistenceException::new);

		AtomicInteger index = new AtomicInteger(0);
		List<VideoInfo> videos = playlist.getVideos().stream()
			.map(v -> new VideoInfo(index.getAndIncrement(), v))
			.toList();

		PlaylistMessage playlistMessage = new PlaylistMessage(videos);
		messagingTemplate.convertAndSend("/sub/messages/rooms/" + roomCode, playlistMessage);
	}

	public void sendVideoSyncInfo(VideoSyncInfoMessage message) {
		messagingTemplate.convertAndSend("/sub/messages/rooms/" + message.getRoomCode(), message);
	}

}
