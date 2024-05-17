package site.youtogether.playlist;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import site.youtogether.exception.playlist.InvalidVideoOrderException;
import site.youtogether.exception.playlist.PlaylistEmptyException;

class PlaylistTest {

	@Test
	@DisplayName("재생 목록에 영상을 추가할 수 있다")
	void addVideoSuccess() {
		// given
		Playlist playlist = new Playlist("room code");
		Video video = createVideo("id", 1L);

		// when
		playlist.add(video);

		// then
		assertThat(playlist.getVideos()).hasSize(1);
		assertThat(playlist.getVideos().get(0).getVideoId()).isEqualTo("id");
	}

	@Test
	@DisplayName("재생 목록에서 영상을 삭제할 수 있다")
	void deleteVideoSuccess() {
		// given
		Playlist playlist = new Playlist("room code");
		Video video1 = createVideo("id-1", 1L);
		Video video2 = createVideo("id-2", 2L);

		playlist.add(video1);
		playlist.add(video2);

		// when
		playlist.delete(video2.getVideoNumber());

		// then
		assertThat(playlist.getVideos()).hasSize(1);
		assertThat(playlist.getVideos().get(0).getVideoId()).isEqualTo("id-1");
	}

	@Test
	@DisplayName("재생 목록의 다음 영상을 재생할 수 있다")
	void playNextVideoSuccess() {
		// given
		Playlist playlist = new Playlist("room code");
		Video video1 = createVideo("id-1", 1L);
		Video video2 = createVideo("id-2", 2L);

		playlist.add(video1);
		playlist.add(video2);

		// when
		playlist.playNextCallByTimer();

		// then
		assertThat(playlist.getVideos()).hasSize(1);
		assertThat(playlist.getVideos().get(0).getVideoId()).isEqualTo("id-2");
	}

	@Test
	@DisplayName("재생 목록이 비어있는 경우 다음 영상을 재생할 수 없다")
	void playNextVideoFail() {
		// given
		Playlist playlist = new Playlist("room code");

		// when / then
		assertThatThrownBy(() -> playlist.playNextCallByTimer())
			.isInstanceOf(PlaylistEmptyException.class);
	}

	@Test
	@DisplayName("재생목록의 순서를 뒤로 변경할 수 있다")
	void playNextReorder() throws Exception {
		// given
		Playlist playlist = new Playlist("room code");

		Video video1 = createVideo("dfllasdf", 1L);
		playlist.add(video1);
		Video video2 = createVideo("12kjfaad", 2L);
		playlist.add(video2);
		Video video3 = createVideo("klovn13l", 3L);
		playlist.add(video3);
		Video video4 = createVideo("qwwe901e", 4L);
		playlist.add(video4);
		Video video5 = createVideo("qwer00ll", 5L);
		playlist.add(video5);

		// when
		playlist.reorderVideo(0, 2);

		// then
		assertThat(playlist.getVideos()).containsExactly(video2, video3, video1, video4, video5);
	}

	@Test
	@DisplayName("재생목록의 순서를 앞으로 변경할 수 있다")
	void playNextReorder2() throws Exception {
		// given
		Playlist playlist = new Playlist("room code");

		Video video1 = createVideo("dfllasdf", 1L);
		playlist.add(video1);
		Video video2 = createVideo("12kjfaad", 2L);
		playlist.add(video2);
		Video video3 = createVideo("klovn13l", 3L);
		playlist.add(video3);
		Video video4 = createVideo("qwwe901e", 4L);
		playlist.add(video4);
		Video video5 = createVideo("qwer00ll", 5L);
		playlist.add(video5);

		// when
		playlist.reorderVideo(4, 1);

		// then
		assertThat(playlist.getVideos()).containsExactly(video1, video5, video2, video3, video4);
	}

	@Test
	@DisplayName("재생목록 길이를 벗어나는 순서 변경은 할 수 없다")
	void playNextReorderInvalid() throws Exception {
		// given
		Playlist playlist = new Playlist("room code");

		Video video1 = createVideo("dfllasdf", 1L);
		playlist.add(video1);
		Video video2 = createVideo("12kjfaad", 2L);
		playlist.add(video2);
		Video video3 = createVideo("klovn13l", 3L);
		playlist.add(video3);
		Video video4 = createVideo("qwwe901e", 4L);
		playlist.add(video4);
		Video video5 = createVideo("qwer00ll", 5L);
		playlist.add(video5);

		// when // then
		assertThatThrownBy(() -> playlist.reorderVideo(-1, 3))
			.isInstanceOf(InvalidVideoOrderException.class);

		assertThatThrownBy(() -> playlist.reorderVideo(1, 25))
			.isInstanceOf(InvalidVideoOrderException.class);
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
