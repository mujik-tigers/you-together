package site.youtogether.playlist;

import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;
import site.youtogether.exception.playlist.PlaylistEmptyException;
import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.application.PlaylistService;

@Getter
public class PlayingVideo {

	private final String roomCode;
	private final String videoId;
	private final long totalTime;
	private double currentTime;
	private Timer timer;
	private final MessageService messageService;
	private final PlaylistService playlistService;

	public PlayingVideo(String roomCode, Video video, MessageService messageService, PlaylistService playlistService) {
		this.roomCode = roomCode;
		this.videoId = video.getVideoId();
		this.totalTime = video.getDuration();
		this.currentTime = 0.0;

		this.messageService = messageService;
		this.playlistService = playlistService;
	}

	public void start(double time) {
		currentTime = Math.round(time * 100) / 100.0;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (currentTime >= totalTime) {
					messageService.sendVideoSyncInfo(
						new VideoSyncInfoMessage(roomCode, PlayerState.END, totalTime, 1.0)
					);
					try {
						playlistService.playNextVideo(roomCode);
					} catch (PlaylistEmptyException ignored) {
					}
					timer.cancel();
					timer.purge();
					return;
				}
				messageService.sendVideoSyncInfo(
					new VideoSyncInfoMessage(roomCode, PlayerState.PLAY, currentTime, 1.0)
				);
				currentTime += 1;
			}
		}, 0, 1000);
	}

}
