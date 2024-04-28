package site.youtogether.playlist;

import static site.youtogether.util.AppConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import com.redis.om.spring.annotations.Document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.exception.playlist.PlaylistIndexOutOfBoundsException;

@Document(value = "playlist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Playlist {

	@Id
	private String roomCode;

	private Video playingVideo;
	private List<Video> videos = new ArrayList<>();

	@TimeToLive
	private final Long expirationTime = TIME_TO_LIVE;

	public Playlist(String roomCode) {
		this.roomCode = roomCode;
	}

	public Video add(Video video) {
		videos.add(video);
		if (playingVideo == null) {
			playNext();
		}
		return playingVideo;
	}

	public Optional<Video> playNext() {
		playingVideo = null;
		if (videos.isEmpty()) {
			return Optional.empty();
		}
		playingVideo = videos.remove(0);
		return Optional.of(playingVideo);
	}

	public void delete(int index) {
		try {
			videos.remove(index);
		} catch (IndexOutOfBoundsException e) {
			throw new PlaylistIndexOutOfBoundsException();
		}
	}

}
