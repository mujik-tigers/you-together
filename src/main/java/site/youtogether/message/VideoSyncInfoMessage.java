package site.youtogether.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import site.youtogether.player.PlayingVideo;
import site.youtogether.player.handler.PlayerState;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class VideoSyncInfoMessage {

	private final MessageType messageType = MessageType.VIDEO_SYNC_INFO;

	@Setter
	private String roomCode;
	private final Long videoNumber;
	private final String videoId;
	private final PlayerState playerState;
	private final double playerCurrentTime;
	private final double playerRate;

	public static VideoSyncInfoMessage ofPlay(PlayingVideo playingVideo, double currentTime) {
		return new VideoSyncInfoMessage(
			playingVideo.roomCode(),
			playingVideo.number(),
			playingVideo.id(),
			PlayerState.PLAY,
			currentTime,
			playingVideo.rate()
		);
	}

	public static VideoSyncInfoMessage ofPause(PlayingVideo playingVideo) {
		return new VideoSyncInfoMessage(
			playingVideo.roomCode(),
			playingVideo.number(),
			playingVideo.id(),
			PlayerState.PAUSE,
			playingVideo.startTime(),
			playingVideo.rate()
		);
	}

	public static VideoSyncInfoMessage ofEnd(PlayingVideo playingVideo) {
		return new VideoSyncInfoMessage(
			playingVideo.roomCode(),
			playingVideo.number(),
			playingVideo.id(),
			PlayerState.END,
			playingVideo.duration(),
			playingVideo.rate()
		);
	}

}
