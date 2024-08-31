package site.youtogether.player.handler;

import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.player.VideoPlayer;

public interface PlayerStateHandler {

	void handle(VideoPlayer videoPlayer, VideoSyncInfoMessage message);

}
