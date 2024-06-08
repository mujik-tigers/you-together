package site.youtogether.playlist;

import java.util.Timer;

import lombok.Getter;
import site.youtogether.exception.playlist.InvalidVideoRateException;
import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.application.PlaylistService;

@Getter
public abstract class PlayingVideo {

	protected final String roomCode;
	protected final String videoId;
	protected final Long videoNumber;
	protected final String videoTitle;
	protected final String channelTitle;
	protected final String thumbnail;
	protected final MessageService messageService;
	protected final PlaylistService playlistService;

	protected double currentTime = 0.0;
	protected Timer timer = new Timer();
	protected double playerRate = 1.0;
	protected long timerPeriod = 1000;

	public PlayingVideo(String roomCode, Video video, MessageService messageService, PlaylistService playlistService) {
		this.roomCode = roomCode;
		this.videoId = video.getVideoId();
		this.videoNumber = video.getVideoNumber();
		this.videoTitle = video.getVideoTitle();
		this.channelTitle = video.getChannelTitle();
		this.thumbnail = video.getThumbnail();

		this.messageService = messageService;
		this.playlistService = playlistService;
	}

	public void startAt(double time) {
		timer.cancel();
		timer.purge();
		currentTime = Math.round(time * 100) / 100.0;
		createTimer(playerRate);
	}

	public void pauseAt(double time) {
		timer.cancel();
		timer.purge();
		currentTime = Math.round(time * 100) / 100.0;

		messageService.sendVideoSyncInfo(
			new VideoSyncInfoMessage(roomCode, videoNumber, videoId, PlayerState.PAUSE, currentTime, playerRate)
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

	protected abstract void createTimer(double playerRate);

}
