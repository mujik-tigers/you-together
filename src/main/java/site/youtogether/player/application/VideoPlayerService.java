package site.youtogether.player.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.player.VideoPlayer;
import site.youtogether.player.handler.PlayerState;
import site.youtogether.player.infrastructure.VideoPlayerStorage;

@Service
@RequiredArgsConstructor
public class VideoPlayerService {

	private final VideoPlayerStorage videoPlayerStorage;

	public void control(VideoSyncInfoMessage message) {
		VideoPlayer videoPlayer = videoPlayerStorage.findByRoomCode(message.getRoomCode());
		PlayerState.handlerOf(message.getPlayerState()).handle(videoPlayer, message);
	}

}
