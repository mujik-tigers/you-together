package site.youtogether.playlist.application;

import java.time.Duration;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.playlist.PlaylistNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.exception.user.VideoEditDeniedException;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.PlayingVideo;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.Video;
import site.youtogether.playlist.dto.PlaylistAddForm;
import site.youtogether.playlist.dto.VideoOrder;
import site.youtogether.playlist.infrastructure.PlayingVideoStorage;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;
import site.youtogether.util.aop.PlaylistSynchronize;

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistStorage playlistStorage;
	private final PlayingVideoStorage playingVideoStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;

	@PlaylistSynchronize
	public void addVideo(Long userId, PlaylistAddForm form) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		if (user.isNotEditable()) {
			throw new VideoEditDeniedException();
		}
		Playlist playlist = playlistStorage.findById(user.getCurrentRoomCode())
			.orElseThrow(PlaylistNoExistenceException::new);

		Video video = createVideo(form);
		playlist.add(video);

		if (!playingVideoStorage.existsById(user.getCurrentRoomCode())) {
			Video nextVideo = playlist.playNext(video.getVideoNumber());
			playingVideoStorage.saveAndPlay(new PlayingVideo(user.getCurrentRoomCode(), nextVideo, messageService, this));
			messageService.sendStartVideoInfo(user.getCurrentRoomCode(), nextVideo.getVideoTitle(), nextVideo.getChannelTitle());
		}
		playlistStorage.save(playlist);

		messageService.sendPlaylist(user.getCurrentRoomCode());
	}

	public void callNextVideoByTimer(String roomCode) {        // PlayingVideo 타이머에 의해 수동적으로 호출되는 메서드
		Playlist playlist = playlistStorage.findById(roomCode)
			.orElseThrow(PlaylistNoExistenceException::new);

		playingVideoStorage.delete(roomCode);                    // 다음에 재생할 영상이 없더라도, 현재 재생중인 영상을 제거해야 하므로, delete 가 선행
		Video nextVideo = playlist.playNextCallByTimer();
		playingVideoStorage.saveAndPlay(new PlayingVideo(roomCode, nextVideo, messageService, this));
		playlistStorage.save(playlist);

		messageService.sendStartVideoInfo(roomCode, nextVideo.getVideoTitle(), nextVideo.getChannelTitle());
		messageService.sendPlaylist(roomCode);
	}

	@PlaylistSynchronize
	public void playNextVideo(Long userId, Long videoNumber) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		if (user.isNotEditable()) {
			throw new VideoEditDeniedException();
		}
		Playlist playlist = playlistStorage.findById(user.getCurrentRoomCode())
			.orElseThrow(PlaylistNoExistenceException::new);

		Video nextVideo = playlist.playNext(videoNumber);
		playingVideoStorage.delete(user.getCurrentRoomCode());            // 다음에 재생할 영상이 올바르지 않은 경우, 현재 재생중인 영상을 제거하면 안되므로, delete 가 후행
		playingVideoStorage.saveAndPlay(new PlayingVideo(user.getCurrentRoomCode(), nextVideo, messageService, this));
		playlistStorage.save(playlist);

		messageService.sendStartVideoInfo(user.getCurrentRoomCode(), nextVideo.getVideoTitle(), nextVideo.getChannelTitle());
		messageService.sendPlaylist(user.getCurrentRoomCode());
	}

	@PlaylistSynchronize
	public void reorderVideo(Long userId, VideoOrder videoOrder) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		if (user.isNotEditable()) {
			throw new VideoEditDeniedException();
		}
		Playlist playlist = playlistStorage.findById(user.getCurrentRoomCode())
			.orElseThrow(PlaylistNoExistenceException::new);

		playlist.reorderVideo(videoOrder.getFrom(), videoOrder.getTo());
		playlistStorage.save(playlist);

		messageService.sendPlaylist(user.getCurrentRoomCode());
	}

	@PlaylistSynchronize
	public void deleteVideo(Long userId, Long videoNumber) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		if (user.isNotEditable()) {
			throw new VideoEditDeniedException();
		}

		Playlist playlist = playlistStorage.findById(user.getCurrentRoomCode())
			.orElseThrow(PlaylistNoExistenceException::new);
		playlist.delete(videoNumber);

		playlistStorage.save(playlist);
		messageService.sendPlaylist(user.getCurrentRoomCode());
	}

	private Video createVideo(PlaylistAddForm form) {
		return Video.builder()
			.videoId(form.getVideoId())
			.videoNumber(RandomUtil.generateVideoNumber())
			.videoTitle(form.getVideoTitle())
			.thumbnail(form.getThumbnail())
			.channelTitle(form.getChannelTitle())
			.duration(Duration.parse(form.getDuration()).getSeconds())
			.build();
	}

}
