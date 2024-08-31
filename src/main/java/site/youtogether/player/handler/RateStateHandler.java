package site.youtogether.player.handler;

import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.player.PlayingVideo;
import site.youtogether.player.VideoPlayer;

public class RateStateHandler implements PlayerStateHandler {

	@Override
	public void handle(VideoPlayer videoPlayer, VideoSyncInfoMessage message) {
		PlayingVideo playingVideo = PlayingVideo.updateRate(videoPlayer.getPlayingVideo(), message.getPlayerRate());

		videoPlayer.switchVideo(playingVideo);
	}

}
