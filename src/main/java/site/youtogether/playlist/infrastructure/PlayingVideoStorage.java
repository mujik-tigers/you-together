package site.youtogether.playlist.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import site.youtogether.playlist.PlayingVideo;

@Repository
public class PlayingVideoStorage {

	private final Map<String, PlayingVideo> storage = new HashMap<>();

	public boolean existsById(String roomCode) {
		return storage.containsKey(roomCode);
	}

	public Optional<PlayingVideo> findById(String roomCode) {
		return Optional.ofNullable(storage.get(roomCode));
	}

	public void saveAndPlay(PlayingVideo playingVideo) {
		deleteIfPresent(playingVideo.getRoomCode());

		storage.put(playingVideo.getRoomCode(), playingVideo);
		playingVideo.start(0);
	}

	public void deleteIfPresent(String roomCode) {
		Optional.ofNullable(storage.remove(roomCode))
			.ifPresent(PlayingVideo::stop);
	}

}
