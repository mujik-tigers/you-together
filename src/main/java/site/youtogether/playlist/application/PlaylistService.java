package site.youtogether.playlist.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.VideoEditDeniedException;
import site.youtogether.message.application.MessageService;
import site.youtogether.player.PlayingVideo;
import site.youtogether.player.VideoPlayer;
import site.youtogether.player.infrastructure.VideoPlayerStorage;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.Video;
import site.youtogether.playlist.dto.VideoForm;
import site.youtogether.playlist.dto.VideoOrder;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;
import site.youtogether.util.aop.PlaylistSynchronize;

@Service
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistStorage playlistStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;
	private final VideoPlayerStorage videoPlayerStorage;

	// 재생 목록에 영상을 추가합니다
	@PlaylistSynchronize
	public void addVideo(Long userId, VideoForm form) {
		User user = getEditableUser(userId);
		String roomCode = user.getCurrentRoomCode();
		Playlist playlist = playlistStorage.getByRoomCode(roomCode);

		Video video = form.toVideo(RandomUtil.generateVideoNumber());
		playlist.addVideo(video);
		playlistStorage.save(playlist);

		messageService.sendPlaylist(roomCode);
	}

	// 현재 재생 중인 영상을 건너뛰고 다음 영상을 재생합니다
	@PlaylistSynchronize
	public void playNextVideo(Long userId, Long videoNumber) {
		User user = getEditableUser(userId);
		String roomCode = user.getCurrentRoomCode();
		Playlist playlist = playlistStorage.getByRoomCode(roomCode);

		Video nextVideo = playlist.getNextVideo(videoNumber);
		PlayingVideo playingVideo = PlayingVideo.from(roomCode, nextVideo);
		VideoPlayer videoPlayer = videoPlayerStorage.findByRoomCode(roomCode);
		videoPlayer.switchVideo(playingVideo);
		playlistStorage.save(playlist);

		messageService.sendStartVideoInfo(roomCode, nextVideo.title(), nextVideo.channelName());
		messageService.sendPlaylist(roomCode);
	}

	// 재생 목록의 영상 순서를 변경합니다
	@PlaylistSynchronize
	public void reorderVideo(Long userId, VideoOrder videoOrder) {
		User user = getEditableUser(userId);
		Playlist playlist = playlistStorage.getByRoomCode(user.getCurrentRoomCode());

		playlist.reorderVideo(videoOrder.from(), videoOrder.to());
		playlistStorage.save(playlist);

		messageService.sendPlaylist(user.getCurrentRoomCode());
	}

	// 영상을 재생 목록에서 삭제합니다
	@PlaylistSynchronize
	public void deleteVideo(Long userId, Long videoNumber) {
		User user = getEditableUser(userId);
		Playlist playlist = playlistStorage.getByRoomCode(user.getCurrentRoomCode());

		playlist.deleteVideo(videoNumber);
		playlistStorage.save(playlist);

		messageService.sendPlaylist(user.getCurrentRoomCode());
	}

	private User getEditableUser(Long id) {
		User user = userStorage.getById(id);

		if (user.isNotEditable())
			throw new VideoEditDeniedException();

		return user;
	}

}
