package site.youtogether.playlist;

import static site.youtogether.util.AppConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import com.redis.om.spring.annotations.Document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(value = "playlist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Playlist {

	@Id
	private String roomCode;

	private final List<Video> videos = new ArrayList<>();

	@TimeToLive
	private final Long expirationTime = TIME_TO_LIVE;

	public void add(Video video) {
		videos.add(video);
	}

	public Playlist(String roomCode) {
		this.roomCode = roomCode;
	}

}
