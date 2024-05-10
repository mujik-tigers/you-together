package site.youtogether.util;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

class DataCleanerTest extends IntegrationTestSupport {

	@Autowired
	private UserStorage userStorage;

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private PlaylistStorage playlistStorage;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private DefaultRedisScript<Void> batchRemoveScript;

	@AfterEach
	void clean() {
		userStorage.deleteAll();
		roomStorage.deleteAll();
		playlistStorage.deleteAll();
	}

	@Test
	@DisplayName("참가자가 없으면서 activate가 true인 방은 activate가 false가 된다")
	void test1() {
		// given
		Room room = Room.builder()
			.title("title")
			.password(null)
			.code("code")
			.capacity(10)
			.activate(true)
			.createdAt(LocalDateTime.of(2024, 5, 10, 12, 0))
			.build();

		roomStorage.save(room);

		// when
		redisTemplate.execute(batchRemoveScript,
			List.of("site.youtogether.room.RoomIdx", "site.youtogether.user.UserIdx", "site.youtogether.playlist.PlaylistIdx"));

		// then
		Room result = roomStorage.findById(room.getCode()).get();

		assertThat(result.isActivate()).isFalse();
	}

	@Test
	@DisplayName("참가자가 없으면서 activate가 false인 방과 방의 재생목록은 삭제된다")
	void test2() {
		Room room = Room.builder()
			.title("title")
			.password(null)
			.code("code")
			.capacity(10)
			.activate(false)
			.createdAt(LocalDateTime.of(2024, 5, 10, 12, 0))
			.build();
		roomStorage.save(room);

		Playlist playlist = new Playlist(room.getCode());
		playlistStorage.save(playlist);

		// when
		redisTemplate.execute(batchRemoveScript,
			List.of("site.youtogether.room.RoomIdx", "site.youtogether.user.UserIdx", "site.youtogether.playlist.PlaylistIdx"));

		// then
		assertThat(roomStorage.existsById(room.getCode())).isFalse();
		assertThat(playlistStorage.existsById(room.getCode())).isFalse();
	}

	@Test
	@DisplayName("참가자가 있는 방은 activate가 true로 유지된다")
	void test3() {
		// given
		Room room = Room.builder()
			.title("title")
			.password(null)
			.code("code")
			.capacity(10)
			.activate(true)
			.createdAt(LocalDateTime.of(2024, 5, 10, 12, 0))
			.build();
		room.enter(null);
		roomStorage.save(room);

		Playlist playlist = new Playlist(room.getCode());
		playlistStorage.save(playlist);

		// when
		redisTemplate.execute(batchRemoveScript,
			List.of("site.youtogether.room.RoomIdx", "site.youtogether.user.UserIdx", "site.youtogether.playlist.PlaylistIdx"));

		// then
		Room result = roomStorage.findById(room.getCode()).get();

		assertThat(result.isActivate()).isTrue();
		assertThat(playlistStorage.existsById(room.getCode())).isTrue();
	}

	@Test
	@DisplayName("방에 입장하지 않은 사용자는 activate가 false가 된다")
	void test4() {
		// given
		User user = User.builder()
			.currentRoomCode(null)
			.nickname("nickname")
			.activate(true)
			.id(1L)
			.build();

		userStorage.save(user);

		// when
		redisTemplate.execute(batchRemoveScript,
			List.of("site.youtogether.room.RoomIdx", "site.youtogether.user.UserIdx", "site.youtogether.playlist.PlaylistIdx"));

		// then
		User result = userStorage.findById(user.getId()).get();

		assertThat(result.isActivate()).isFalse();
	}

	@Test
	@DisplayName("activate가 false인 사용자는 삭제된다")
	void test5() {
		User user = User.builder()
			.currentRoomCode(null)
			.nickname("nickname")
			.activate(false)
			.id(1L)
			.build();

		userStorage.save(user);

		// when
		redisTemplate.execute(batchRemoveScript,
			List.of("site.youtogether.room.RoomIdx", "site.youtogether.user.UserIdx", "site.youtogether.playlist.PlaylistIdx"));

		// then
		assertThat(userStorage.existsById(user.getId())).isFalse();
	}

	@Test
	@DisplayName("참가 중인 사용자는 activate가 true로 유지된다")
	void test6() {
		User user = User.builder()
			.currentRoomCode("room code")
			.nickname("nickname")
			.activate(true)
			.id(1L)
			.build();

		userStorage.save(user);

		// when
		redisTemplate.execute(batchRemoveScript,
			List.of("site.youtogether.room.RoomIdx", "site.youtogether.user.UserIdx", "site.youtogether.playlist.PlaylistIdx"));

		// then
		User result = userStorage.findById(user.getId()).get();

		assertThat(result.isActivate()).isTrue();
	}

}
