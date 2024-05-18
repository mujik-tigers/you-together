package site.youtogether.playlist.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.playlist.PlayingVideoNoExistenceException;
import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.playlist.PlayerState;
import site.youtogether.playlist.PlayingVideo;
import site.youtogether.playlist.infrastructure.PlayingVideoStorage;

@Service
@RequiredArgsConstructor
public class PlayingVideoService {

	private final PlayingVideoStorage playingVideoStorage;

	public void manageVideo(VideoSyncInfoMessage videoSyncInfoMessage) {
		PlayingVideo playingVideo = playingVideoStorage.findById(videoSyncInfoMessage.getRoomCode())
			.orElseThrow(PlayingVideoNoExistenceException::new);

		PlayerState playerState = videoSyncInfoMessage.getPlayerState();
		if (playerState == PlayerState.PAUSE) {
			playingVideo.pauseAt(videoSyncInfoMessage.getPlayerCurrentTime());
		} else if (playerState == PlayerState.RATE) {
			playingVideo.changeRate(videoSyncInfoMessage.getPlayerRate());
		} else if (playerState == PlayerState.PLAY) {
			playingVideo.startAt(videoSyncInfoMessage.getPlayerCurrentTime());
		}
	}

}
