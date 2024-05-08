package site.youtogether.playlist.dto;

import lombok.Getter;
import site.youtogether.playlist.Video;

@Getter
public class VideoInfo {

	private final int index;
	private final Long videoNumber;
	private final String videoTitle;
	private final String thumbnail;
	private final String channelTitle;

	public VideoInfo(int index, Video video) {
		this.index = index;
		this.videoNumber = video.getVideoNumber();
		this.videoTitle = video.getVideoTitle();
		this.thumbnail = video.getThumbnail();
		this.channelTitle = video.getChannelTitle();
	}

}
