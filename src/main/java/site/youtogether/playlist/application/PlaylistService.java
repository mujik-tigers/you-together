package site.youtogether.playlist.application;

import java.time.Duration;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.playlist.PlaylistEmptyException;
import site.youtogether.exception.playlist.PlaylistNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
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

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistStorage playlistStorage;
	private final PlayingVideoStorage playingVideoStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;

	public void addVideo(Long userId, PlaylistAddForm form) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		// if (user.isNotEditable()) {
		// 	throw new VideoEditDeniedException();
		// }

		Playlist playlist = playlistStorage.findById(user.getCurrentRoomCode())
			.orElseThrow(PlaylistNoExistenceException::new);
		Video video = createVideo(form);
		Video nextVideo = playlist.add(video);

		if (!playingVideoStorage.existsById(user.getCurrentRoomCode())) {
			playingVideoStorage.saveAndPlay(new PlayingVideo(user.getCurrentRoomCode(), nextVideo, messageService, this));
		}

		playlistStorage.save(playlist);
		messageService.sendPlaylist(user.getCurrentRoomCode());
	}

	public void playNextVideo(String roomCode) {        // PlayingVideo 타이머에 의해 수동적으로 호출되는 메서드
		Playlist playlist = playlistStorage.findById(roomCode)
			.orElseThrow(PlaylistNoExistenceException::new);
		Video nextVideo = playlist.playNext()
			.orElseThrow(() -> {
				playingVideoStorage.delete(roomCode);
				playlistStorage.save(playlist);
				return new PlaylistEmptyException();
			});

		playingVideoStorage.saveAndPlay(new PlayingVideo(roomCode, nextVideo, messageService, this));

		playlistStorage.save(playlist);
		messageService.sendPlaylist(roomCode);
	}

	public void reorderVideo(Long userId, VideoOrder videoOrder) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		// if (user.isNotEditable()) {
		// 	throw new VideoEditDeniedException();
		// }

		Playlist playlist = playlistStorage.findById(user.getCurrentRoomCode())
			.orElseThrow(PlaylistNoExistenceException::new);
		playlist.reorderVideo(videoOrder.getFrom(), videoOrder.getTo());
		playlistStorage.save(playlist);

		messageService.sendPlaylist(user.getCurrentRoomCode());
	}

	public void deleteVideo(Long userId, int videoIndex) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		// if (user.isNotEditable()) {
		// 	throw new VideoEditDeniedException();
		// }

		Playlist playlist = playlistStorage.findById(user.getCurrentRoomCode())
			.orElseThrow(PlaylistNoExistenceException::new);
		playlist.delete(videoIndex);

		playlistStorage.save(playlist);
		messageService.sendPlaylist(user.getCurrentRoomCode());
	}

	private Video createVideo(PlaylistAddForm form) {
		return Video.builder()
			.videoId(form.getVideoId())
			.videoTitle(form.getVideoTitle())
			.thumbnail(form.getThumbnail())
			.channelTitle(form.getChannelTitle())
			.duration(Duration.parse(form.getDuration()).getSeconds())
			.build();
	}

}
