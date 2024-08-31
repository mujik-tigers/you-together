package site.youtogether.player.application;

import java.util.Timer;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.player.PlayingVideo;

@Component
@RequiredArgsConstructor
public class VideoSynchronizer {

	private static final long NO_DELAY = 0;
	private static final long ONE_SECOND = 1_000;

	private final MessageService messageService;

	public void pause(Timer timer, PlayingVideo playingVideo) {
		timer.cancel();

		// 일시정지 메세지를 1회 전송합니다
		messageService.sendVideoSyncInfo(VideoSyncInfoMessage.ofPause(playingVideo));
	}

	public void play(Timer timer, PlayingVideo playingVideo) {
		// 1초마다 재생 메세지를 전송합니다
		timer.scheduleAtFixedRate(new SynchronizerTask(playingVideo) {
			@Override
			public void run() {
				if (this.isLiveStream() || this.getCurrentTime() < playingVideo.duration()) {
					messageService.sendVideoSyncInfo(this.getSyncMessage());
				}

				this.incrementTime();
			}
		}, NO_DELAY, Math.round(ONE_SECOND / playingVideo.rate()));
	}

	public void stop(Timer timer, PlayingVideo playingVideo) {
		timer.cancel();

		// 종료 메세지를 1회 전송합니다
		messageService.sendVideoSyncInfo(VideoSyncInfoMessage.ofEnd(playingVideo));
	}

}
