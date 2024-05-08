package site.youtogether.playlist;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Video {

	private final String videoId;
	private final Long videoNumber;
	private final long duration;
	private final String thumbnail;
	private final String videoTitle;
	private final String channelTitle;

	@Builder
	public Video(String videoId, Long videoNumber, long duration, String thumbnail, String videoTitle, String channelTitle) {
		this.videoId = videoId;
		this.videoNumber = videoNumber;
		this.duration = duration;
		this.thumbnail = thumbnail;
		this.videoTitle = videoTitle;
		this.channelTitle = channelTitle;
	}

}
