package site.youtogether.player.handler;

import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.player.VideoPlayer;

public class EndStateHandler implements PlayerStateHandler {

	@Override
	public void handle(VideoPlayer videoPlayer, VideoSyncInfoMessage message) {
		videoPlayer.stopVideo();
	}

}
