package site.youtogether.playlist.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PlaylistAddForm {

	private final String videoId;
	private final String videoTitle;
	private final String channelTitle;
	private final String thumbnail;
	private final String duration;

}
