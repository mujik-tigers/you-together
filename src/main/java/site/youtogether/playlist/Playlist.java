package site.youtogether.playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.exception.playlist.InvalidVideoNumberException;
import site.youtogether.exception.playlist.InvalidVideoOrderException;
import site.youtogether.exception.playlist.PlaylistEmptyException;

@Document(value = "playlist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Playlist {

	@Id
	private String roomCode;

	private List<Video> videos = new ArrayList<>();

	public Playlist(String roomCode) {
		this.roomCode = roomCode;
	}

	public void add(Video video) {
		videos.add(video);
	}

	public Video playNext(Long videoNumber) {
		if (videos.isEmpty()) {
			throw new PlaylistEmptyException();
		}

		if (!videos.get(0).getVideoNumber().equals(videoNumber)) {
			throw new InvalidVideoNumberException();
		}

		return videos.remove(0);
	}

	public Video playNextCallByTimer() {            // PlayingVideo 타이머에 의해 수동적으로 호출되는 메서드
		if (videos.isEmpty()) {
			throw new PlaylistEmptyException();
		}

		return videos.remove(0);
	}

	public void delete(Long videoNumber) {
		videos = videos.stream()
			.filter(v -> !v.getVideoNumber().equals(videoNumber))
			.collect(Collectors.toList());
	}

	public void reorderVideo(int from, int to) {
		try {
			Video video = videos.get(from);
			videos.remove(from);
			videos.add(to, video);
		} catch (IndexOutOfBoundsException e) {
			throw new InvalidVideoOrderException();
		}
	}

}
