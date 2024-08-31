package site.youtogether.player;

import static site.youtogether.player.handler.PlayerState.*;

import java.util.Timer;

import site.youtogether.player.application.VideoSynchronizer;
import site.youtogether.player.handler.PlayerState;

public class VideoPlayer {

	private final String roomCode;
	private final VideoSynchronizer videoSynchronizer;
	private final Timer timer;
	private PlayerState state;
	private PlayingVideo playingVideo;

	public VideoPlayer(String roomCode, VideoSynchronizer videoSynchronizer) {
		this.roomCode = roomCode;
		this.videoSynchronizer = videoSynchronizer;
		this.timer = new Timer();
		this.state = END;
	}

	// 영상을 일시정지합니다
	public void pauseVideo(PlayingVideo playingVideo) {
		if (playingVideo == null)
			return;

		videoSynchronizer.pause(timer, playingVideo);
		state = PAUSE;
	}

	// 영상을 재생합니다
	public void playVideo(PlayingVideo playingVideo) {
		switchVideo(playingVideo);
		videoSynchronizer.play(timer, playingVideo);
		state = PLAY;
	}

	public void stopVideo() {
		if (playingVideo != null) {
			videoSynchronizer.stop(timer, playingVideo);
			state = END;
			playingVideo = null;
		}
	}

	public void switchVideo(PlayingVideo playingVideo) {
		boolean wasPlaying = state == PLAY;
		boolean isNewVideo = !this.playingVideo.number().equals(playingVideo.number());
		// 재생 중인 영상이 있다면 우선 중지합니다
		stopVideo();
		this.playingVideo = playingVideo;

		if (wasPlaying || isNewVideo) {
			playVideo(playingVideo);
		}
	}

	public String getRoomCode() {
		return this.roomCode;
	}

	public PlayingVideo getPlayingVideo() {
		return playingVideo;
	}

}
