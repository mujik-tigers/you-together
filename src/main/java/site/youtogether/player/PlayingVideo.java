package site.youtogether.player;

import site.youtogether.playlist.Video;

public record PlayingVideo(
	String roomCode,
	String id,
	Long number,
	double startTime,
	double rate,
	double duration) {

	private static final double ZERO_SECOND = 0.0;
	private static final double DEFAULT_RATE = 1.0;

	public static PlayingVideo updateStartTime(PlayingVideo playingVideo, double startTime) {
		return new PlayingVideo(
			playingVideo.roomCode(),
			playingVideo.id(),
			playingVideo.number(),
			startTime,
			playingVideo.rate(),
			playingVideo.duration());
	}

	public static PlayingVideo updateRate(PlayingVideo playingVideo, double rate) {
		return new PlayingVideo(
			playingVideo.roomCode(),
			playingVideo.id(),
			playingVideo.number(),
			playingVideo.startTime(),
			rate,
			playingVideo.duration());
	}

	public static PlayingVideo from(String roomCode, Video video) {
		return new PlayingVideo(
			roomCode,
			video.id(),
			video.number(),
			ZERO_SECOND,
			DEFAULT_RATE,
			video.duration()
		);
	}

}
