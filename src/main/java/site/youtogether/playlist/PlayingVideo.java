package site.youtogether.playlist;

import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;
import site.youtogether.exception.playlist.InvalidVideoRateException;
import site.youtogether.exception.playlist.PlaylistEmptyException;
import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.application.PlaylistService;

@Getter
public class PlayingVideo {

	private final String roomCode;
	private final String videoId;
	private final String videoTitle;
	private final String channelTitle;
	private final String thumbnail;
	private final long totalTime;
	private final MessageService messageService;
	private final PlaylistService playlistService;

	private double currentTime;
	private Timer timer = new Timer();
	private double playerRate = 1.0;
	private long timerPeriod = 1000;

	public PlayingVideo(String roomCode, Video video, MessageService messageService, PlaylistService playlistService) {
		this.roomCode = roomCode;
		this.videoId = video.getVideoId();
		this.videoTitle = video.getVideoTitle();
		this.channelTitle = video.getChannelTitle();
		this.thumbnail = video.getThumbnail();
		this.totalTime = video.getDuration();
		this.currentTime = 0.0;

		this.messageService = messageService;
		this.playlistService = playlistService;
	}

	public void start(double time) {
		timer.cancel();
		timer.purge();
		currentTime = Math.round(time * 100) / 100.0;
		createTimer(playerRate);
	}

	public void pause(double time) {
		timer.cancel();
		timer.purge();
		currentTime = Math.round(time * 100) / 100.0;

		messageService.sendVideoSyncInfo(
			new VideoSyncInfoMessage(roomCode, videoId, PlayerState.PAUSE, currentTime, playerRate)
		);
	}

	public void stop() {
		timer.cancel();
		timer.purge();
	}

	public void changeRate(double playerRate) {
		if (playerRate < 0.25 || playerRate > 2 || (int)(playerRate * 100) % 5 != 0) {
			throw new InvalidVideoRateException();
		}
		timer.cancel();
		timer.purge();

		this.playerRate = playerRate;
		this.timerPeriod = Math.round(1000 / playerRate);
		createTimer(playerRate);
	}

	public void changeCurrentTime(double time) {
		this.currentTime = time;
	}

	private void createTimer(double playerRate) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (currentTime >= totalTime) {
					messageService.sendVideoSyncInfo(
						new VideoSyncInfoMessage(roomCode, videoId, PlayerState.END, totalTime, playerRate)
					);
					try {
						playlistService.callNextVideoByTimer(roomCode);
					} catch (PlaylistEmptyException ignored) {
					}
					timer.cancel();
					timer.purge();
					return;
				}
				messageService.sendVideoSyncInfo(
					new VideoSyncInfoMessage(roomCode, videoId, PlayerState.PLAY, currentTime, playerRate)
				);
				currentTime += 1;
			}
		}, 0, timerPeriod);
	}

}
