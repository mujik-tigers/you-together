package site.youtogether.playlist.infrastructure;

import com.redis.om.spring.repository.RedisDocumentRepository;

import site.youtogether.exception.playlist.PlaylistNoExistenceException;
import site.youtogether.playlist.Playlist;

public interface PlaylistStorage extends RedisDocumentRepository<Playlist, String> {

	default Playlist getByRoomCode(String roomCode) {
		return findById(roomCode).orElseThrow(PlaylistNoExistenceException::new);
	}

}
