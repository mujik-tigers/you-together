package site.youtogether.playlist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.exception.playlist.InvalidVideoNumberException;
import site.youtogether.exception.playlist.InvalidVideoOrderException;
import site.youtogether.exception.playlist.PlaylistEmptyException;

@Document(value = "playlist")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Playlist {

	/**
	 * 재생 목록
	 * <p> key : roomCode
	 * <p> - 재생 목록 조회
	 * <p> - 재생 목록에 영상 추가
	 * <p> - 재생 목록의 영상 순서 변경
	 * <p> - 재생 목록의 영상 삭제
	 */

	private static final int FIRST = 0;

	@Id
	private String roomCode;

	private final List<Video> videos = new ArrayList<>();

	// 재생 목록의 맨 끝에 영상을 추가합니다
	public void addVideo(Video video) {
		videos.add(video);
	}

	// 다음 영상을 제공합니다
	public Video getNextVideo(Long nextVideoNumber) {
		validateNextVideoNumber(nextVideoNumber);

		return videos.remove(FIRST);
	}

	// 특정 영상을 삭제합니다
	// TODO: 시간 복잡도를 개선할 방법이 있는지 알아보기
	public void deleteVideo(Long videoNumber) {
		for (int i = 0; i < videos.size(); i++) {
			if (videos.get(i).matches(videoNumber)) {
				videos.remove(i);
				break;
			}
		}
	}

	// 영상의 순서를 변경합니다
	public void reorderVideo(int from, int to) {
		validateVideoOrder(from, to);

		Video video = videos.get(from);
		videos.remove(from);
		videos.add(to, video);
	}

	private void validateNextVideoNumber(Long nextVideoNumber) {
		if (videos.isEmpty())
			throw new PlaylistEmptyException();

		if (videos.get(FIRST).doesNotMatch(nextVideoNumber))
			throw new InvalidVideoNumberException();
	}

	private void validateVideoOrder(int from, int to) {
		if (from < 0 || to < 0 || from >= videos.size() || to >= videos.size() || from == to)
			throw new InvalidVideoOrderException();
	}

}
