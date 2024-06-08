package site.youtogether.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import site.youtogether.playlist.PlayerState;

@AllArgsConstructor
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

}
