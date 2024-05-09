package site.youtogether.playlist;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
		playlist.playNext(video1.getVideoNumber());

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
		assertThatThrownBy(() -> playlist.playNext(1L))
			.isInstanceOf(PlaylistEmptyException.class);
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
