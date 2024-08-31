package site.youtogether.playlist.dto;

import site.youtogether.playlist.Video;

public record VideoInfo(
	Long videoNumber,
	String videoTitle,
	String thumbnail,
	String channelTitle) {

	public static VideoInfo from(Video video) {
		return new VideoInfo(
			video.number(),
			video.title(),
			video.thumbnail(),
			video.channelName()
		);
	}

}
