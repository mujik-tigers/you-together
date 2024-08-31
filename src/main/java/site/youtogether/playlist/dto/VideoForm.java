package site.youtogether.playlist.dto;

import java.time.Duration;

import site.youtogether.playlist.Video;

public record VideoForm(
	String videoId,
	String videoTitle,
	String channelTitle,
	String thumbnail,
	String duration) {

	public Video toVideo(Long number) {
		return new Video(
			videoId,
			number,
			thumbnail,
			videoTitle,
			channelTitle,
			Duration.parse(duration).getSeconds()
		);
	}

}
