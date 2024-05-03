package site.youtogether.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.youtogether.playlist.PlayerState;

@RequiredArgsConstructor
@Getter
public class VideoSyncInfoMessage {

	private final MessageType messageType = MessageType.VIDEO_SYNC_INFO;

	private final String roomCode;
	private final String videoId;
	private final PlayerState playerState;
	private final double playerCurrentTime;
	private final double playerRate;

}
