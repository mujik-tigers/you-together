package site.youtogether.playlist.application;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.exception.user.VideoEditDeniedException;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.Video;
import site.youtogether.playlist.dto.PlaylistAddForm;
import site.youtogether.playlist.dto.VideoOrder;
import site.youtogether.playlist.infrastructure.PlayingVideoStorage;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

class PlaylistServiceTest extends IntegrationTestSupport {

	@Autowired
	private PlaylistService playlistService;

	@Autowired
	private PlayingVideoStorage playingVideoStorage;

	@Autowired
	private UserStorage userStorage;

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private PlaylistStorage playlistStorage;

	@AfterEach
	void clear() {
		roomStorage.deleteAll();
		userStorage.deleteAll();
		playlistStorage.deleteAll();
	}

	@Test
	@DisplayName("재생 목록에 영상을 추가할 수 있다")
	void addVideoSuccess() {
		// given
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		User host = createRoomAndEnterUser(roomCode, Role.HOST);

		PlaylistAddForm form = new PlaylistAddForm("video id", "title", "channel", "thumbnail", Duration.ofSeconds(10).toString());

		// when
		playlistService.addVideo(host.getId(), form);
		playlistService.addVideo(host.getId(), form);

		// then
		Playlist result = playlistStorage.findById(roomCode).get();

		assertThat(playingVideoStorage.existsById(roomCode)).isTrue();
		assertThat(result.getVideos()).hasSize(1);
	}

	@Test
	@DisplayName("빈 재생 목록에 영상을 추가하면 바로 재생된다")
	void addVideoAndPlaySuccess() {
		// given
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		User host = createRoomAndEnterUser(roomCode, Role.HOST);

		PlaylistAddForm form = new PlaylistAddForm("video id", "title", "channel", "thumbnail", Duration.ofSeconds(10).toString());

		// when
		playlistService.addVideo(host.getId(), form);

		// then
		Playlist result = playlistStorage.findById(roomCode).get();

		assertThat(playingVideoStorage.existsById(roomCode)).isTrue();
		assertThat(result.getVideos()).isEmpty();
	}

	@ParameterizedTest
	@EnumSource(value = Role.class, names = {"EDITOR", "MANAGER", "HOST"})
	@DisplayName("EDITOR 이상의 역할을 갖는 사용자는 재생 목록을 수정할 수 있다")
	void isEditable(Role role) {
		// given
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		User editableUser = createRoomAndEnterUser(roomCode, role);

		PlaylistAddForm form = new PlaylistAddForm("video id", "title", "channel", "thumbnail", Duration.ofSeconds(10).toString());

		// when
		playlistService.addVideo(editableUser.getId(), form);
		playlistService.addVideo(editableUser.getId(), form);

		// then
		Playlist result = playlistStorage.findById(roomCode).get();

		assertThat(playingVideoStorage.existsById(roomCode)).isTrue();
		assertThat(result.getVideos()).hasSize(1);
	}

	@ParameterizedTest
	@EnumSource(value = Role.class, names = {"GUEST", "VIEWER"})
	@DisplayName("EDITOR 미만의 역할을 갖는 사용자는 재생 목록을 수정할 수 없다")
	void isNotEditable(Role role) {
		// given
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		User notEditableUser = createRoomAndEnterUser(roomCode, role);

		PlaylistAddForm form = new PlaylistAddForm("video id", "title", "channel", "thumbnail", Duration.ofSeconds(10).toString());

		// when / then
		assertThatThrownBy(() -> playlistService.addVideo(notEditableUser.getId(), form))
			.isInstanceOf(VideoEditDeniedException.class);
	}

	@Test
	@DisplayName("재생 목록의 다음 영상을 재생할 수 있다")
	void playNextVideoSuccess() {
		// given
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		User editor = createRoomAndEnterUser(roomCode, Role.EDITOR);

		PlaylistAddForm form = new PlaylistAddForm("video id", "title", "channel", "thumbnail", Duration.ofMinutes(10).toString());
		playlistService.addVideo(editor.getId(), form);
		playlistService.addVideo(editor.getId(), form);

		Playlist playlist = playlistStorage.findById(roomCode).get();
		Video nextVideo = playlist.getVideos().get(0);

		// when
		playlistService.playNextVideo(editor.getId(), nextVideo.getVideoNumber());

		// then
		Playlist result = playlistStorage.findById(roomCode).get();

		assertThat(playingVideoStorage.existsById(roomCode)).isTrue();
		assertThat(result.getVideos()).hasSize(0);
	}

	@Test
	@DisplayName("재생 목록에서 영상을 삭제할 수 있다")
	void deleteVideoSuccess() {
		// given
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		User editor = createRoomAndEnterUser(roomCode, Role.EDITOR);

		PlaylistAddForm form = new PlaylistAddForm("video id", "title", "channel", "thumbnail", Duration.ofSeconds(10).toString());
		playlistService.addVideo(editor.getId(), form);
		playlistService.addVideo(editor.getId(), form);
		playlistService.addVideo(editor.getId(), form);

		Video video = playlistStorage.findById(roomCode).get().getVideos().get(1);

		// when
		playlistService.deleteVideo(editor.getId(), video.getVideoNumber());

		// then
		Playlist result = playlistStorage.findById(roomCode).get();

		assertThat(playingVideoStorage.existsById(roomCode)).isTrue();
		assertThat(result.getVideos()).hasSize(1);
	}

	@Test
	@DisplayName("재생 목록에서 영상 순서를 변경할 수 있다")
	void reorderVideo() throws Exception {
		// given
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		User editor = createRoomAndEnterUser(roomCode, Role.EDITOR);
		Playlist playlist = playlistStorage.findById(roomCode).get();

		Video video1 = createVideo("video1", 1L);
		playlist.add(video1);
		Video video2 = createVideo("video2", 2L);
		playlist.add(video2);
		Video video3 = createVideo("video3", 3L);
		playlist.add(video3);
		Video video4 = createVideo("video4", 4L);
		playlist.add(video4);

		playlistStorage.save(playlist);

		// when
		playlistService.reorderVideo(editor.getId(), new VideoOrder(0, 3));

		// then
		Playlist result = playlistStorage.findById(roomCode).get();
		assertThat(result.getVideos()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(video2, video3, video4, video1);
	}

	private User createRoomAndEnterUser(String roomCode, Role role) {
		User user = User.builder()
			.id(1L)
			.currentRoomCode(roomCode)
			.nickname("nickname")
			.build();

		Room room = Room.builder()
			.title("title")
			.capacity(5)
			.createdAt(LocalDateTime.of(2024, 5, 4, 16, 0))
			.password(null)
			.build();

		Playlist playlist = new Playlist(roomCode);

		if (role == Role.HOST) {
			user.createRoom(roomCode);
		}

		user.getHistory().put(roomCode, role);
		room.enter(null);

		userStorage.save(user);
		roomStorage.save(room);
		playlistStorage.save(playlist);

		return user;
	}

	private Video createVideo(String id, Long number) {
		return Video.builder()
			.videoId(id)
			.videoNumber(number)
			.videoTitle("title")
			.channelTitle("channel")
			.duration(10L)
			.thumbnail("thumbnail")
			.build();
	}

}
