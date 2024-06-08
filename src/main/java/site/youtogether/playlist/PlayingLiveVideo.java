package site.youtogether.playlist;

import java.util.Timer;
import java.util.TimerTask;

import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.application.PlaylistService;

public class PlayingLiveVideo extends PlayingVideo {

	public PlayingLiveVideo(String roomCode, Video video, MessageService messageService, PlaylistService playlistService) {
		super(roomCode, video, messageService, playlistService);
	}

	@Override
	protected void createTimer(double playerRate) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				messageService.sendVideoSyncInfo(
					new VideoSyncInfoMessage(roomCode, videoNumber, videoId, PlayerState.PLAY, currentTime, playerRate)
				);
				currentTime += 1;
			}
		}, 0, timerPeriod);
	}

}
