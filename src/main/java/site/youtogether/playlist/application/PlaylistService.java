package site.youtogether.playlist.application;

import java.time.Duration;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.playlist.PlaylistNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.exception.user.VideoEditDeniedException;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.Video;
import site.youtogether.playlist.dto.PlaylistAddForm;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistStorage playlistStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;

	public void addVideo(Long userId, PlaylistAddForm form) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		if (user.hasLowerRoleThan(Role.EDITOR)) {
			throw new VideoEditDeniedException();
		}

		Playlist playlist = playlistStorage.findById(form.getRoomCode())
			.orElseThrow(PlaylistNoExistenceException::new);

		Video video = Video.builder()
			.videoId(form.getVideoId())
			.videoTitle(form.getVideoTitle())
			.thumbnail(form.getThumbnail())
			.channelTitle(form.getChannelTitle())
			.duration(Duration.parse(form.getDuration()).getSeconds())
			.build();

		playlist.add(video);
		playlistStorage.save(playlist);

		messageService.sendPlaylist(form.getRoomCode());
	}

}
