package site.youtogether.playlist.dto;

import lombok.Getter;
import site.youtogether.playlist.Video;

@Getter
public class VideoInfo {

	private final Long videoNumber;
	private final String videoTitle;
	private final String thumbnail;
	private final String channelTitle;

	public VideoInfo(Video video) {
		this.videoNumber = video.getVideoNumber();
		this.videoTitle = video.getVideoTitle();
		this.thumbnail = video.getThumbnail();
		this.channelTitle = video.getChannelTitle();
	}

}
