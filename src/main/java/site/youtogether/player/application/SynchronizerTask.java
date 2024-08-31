package site.youtogether.player.application;

import java.util.TimerTask;

import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.player.PlayingVideo;

public abstract class SynchronizerTask extends TimerTask {

	private static final double EMPTY = 0.0;

	private final PlayingVideo playingVideo;
	private double currentTime;

	public SynchronizerTask(PlayingVideo playingVideo) {
		this.playingVideo = playingVideo;
		this.currentTime = playingVideo.startTime();
	}

	public VideoSyncInfoMessage getSyncMessage() {
		return VideoSyncInfoMessage.ofPlay(playingVideo, currentTime);
	}

	public boolean isLiveStream() {
		return playingVideo.duration() == EMPTY;
	}

	public void incrementTime() {
		++currentTime;
	}

	public double getCurrentTime() {
		return currentTime;
	}

}
