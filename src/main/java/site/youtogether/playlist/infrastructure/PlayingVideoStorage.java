package site.youtogether.playlist.infrastructure;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import site.youtogether.playlist.PlayingVideo;

@Repository
public class PlayingVideoStorage {

	private final Map<String, PlayingVideo> storage = new HashMap<>();

	public boolean existsById(String roomCode) {
		return storage.containsKey(roomCode);
	}

	public void saveAndPlay(PlayingVideo playingVideo) {
		storage.put(playingVideo.getRoomCode(), playingVideo);
		playingVideo.start(0);
	}

	public void delete(String roomCode) {
		storage.remove(roomCode);
	}

}
