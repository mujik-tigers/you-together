package site.youtogether.playlist;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Video {

	private final Long videoNumber;
	private final String videoId;
	private final long duration;
	private final String thumbnail;
	private final String videoTitle;
	private final String channelTitle;

	@Builder
	public Video(Long videoNumber, String videoId, long duration, String thumbnail, String videoTitle, String channelTitle) {
		this.videoNumber = videoNumber;
		this.videoId = videoId;
		this.duration = duration;
		this.thumbnail = thumbnail;
		this.videoTitle = videoTitle;
		this.channelTitle = channelTitle;
	}

	public boolean isLiveStreaming() {
		return duration == 0;
	}

}
