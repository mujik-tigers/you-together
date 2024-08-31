package site.youtogether.player.infrastructure;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import site.youtogether.player.VideoPlayer;
import site.youtogether.player.application.VideoSynchronizer;

@Repository
@RequiredArgsConstructor
public class VideoPlayerStorage {

	// key: String roomCode - value: VideoPlayer
	private final Map<String, VideoPlayer> storage = new ConcurrentHashMap<>();
	private final VideoSynchronizer videoSynchronizer;

	public boolean existsByRoomCode(String roomCode) {
		return storage.containsKey(roomCode);
	}

	public VideoPlayer findByRoomCode(String roomCode) {
		return Optional.ofNullable(storage.get(roomCode))
			.orElseGet(() -> new VideoPlayer(roomCode, videoSynchronizer));
	}

	public VideoPlayer save(VideoPlayer videoPlayer) {
		// VideoPlayer를 덮어 쓰기 전에 동작 중인 타이머가 있다면 종료해야 합니다
		deleteByRoomCode(videoPlayer.getRoomCode());
		storage.put(videoPlayer.getRoomCode(), videoPlayer);

		return videoPlayer;
	}

	public void deleteByRoomCode(String roomCode) {
		// VideoPlayer를 지울 때 동작 중인 타이머가 있다면 종료해야 합니다
		Optional.ofNullable(storage.remove(roomCode))
			.ifPresent(VideoPlayer::stopVideo);
	}

}
