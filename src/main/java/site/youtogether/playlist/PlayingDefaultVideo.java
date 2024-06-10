package site.youtogether.playlist;

import java.util.Timer;
import java.util.TimerTask;

import site.youtogether.exception.playlist.PlaylistEmptyException;
import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.application.PlaylistService;

public class PlayingDefaultVideo extends PlayingVideo {

	private final long totalTime;

	public PlayingDefaultVideo(String roomCode, Video video, MessageService messageService, PlaylistService playlistService) {
		super(roomCode, video, messageService, playlistService);
		this.totalTime = video.getDuration();
	}

	@Override
	protected void createTimer(double playerRate) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (currentTime >= totalTime) {
					try {
						playlistService.callNextVideoByTimer(roomCode);
					} catch (PlaylistEmptyException ignored) {
					}
					timer.cancel();
					timer.purge();
					return;
				}
				messageService.sendVideoSyncInfo(
					new VideoSyncInfoMessage(roomCode, videoNumber, videoId, PlayerState.PLAY, currentTime, playerRate)
				);
				currentTime += 1;
			}
		}, 0, timerPeriod);
	}

}
