package site.youtogether.util.aop;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.application.PlaylistService;
import site.youtogether.playlist.dto.PlaylistAddForm;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.room.Room;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

class ConcurrencyHandlingAspectTest extends IntegrationTestSupport {

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private UserStorage userStorage;

	@Autowired
	private PlaylistStorage playlistStorage;

	@Autowired
	private PlaylistService playlistService;

	@AfterEach
	void clean() {
		roomStorage.deleteAll();
		userStorage.deleteAll();
		playlistStorage.deleteAll();
	}

	@Test
	@DisplayName("동시에 100명 입장")
	void enterMultiThread() throws InterruptedException {
		Room room = createRoom(100);
		createBulkUsers(150);

		int threadCount = 150;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (long i = 0; i < threadCount; i++) {
			long userId = i;
			executorService.submit(() -> {
				try {
					roomService.enter("room code", userId, null);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		Room savedRoom = roomStorage.findById("room code").get();
		assertThat(savedRoom.getParticipantCount()).isEqualTo(100);
	}

	@Test
	@DisplayName("동시에 100명이 퇴장")
	void leaveMultiThread() throws Exception {
		Room room = createRoom(120);
		createAndEnterBulkUsers(120, room.getCode());

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (long i = 0; i < threadCount; i++) {
			long userId = i;
			executorService.submit(() -> {
				try {
					roomService.leave(userId);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		Room savedRoom = roomStorage.findById(room.getCode()).get();
		assertThat(savedRoom.getParticipantCount()).isEqualTo(20);
	}

	@Test
	@DisplayName("동시에 50명이 플레이리스트에 영상 추가")
	void addPlaylistMultiThread() throws Exception {
		Room room = createRoom(50);
		createAndEnterBulkUsers(50, room.getCode());

		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (long i = 0; i < threadCount; i++) {
			long userId = i;

			PlaylistAddForm form = new PlaylistAddForm("videoId." + i, "title" + i, "channel" + i, "thumbnail", Duration.ofMinutes(10).toString());

			executorService.submit(() -> {
				try {
					playlistService.addVideo(userId, form);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		Playlist savedPlaylist = playlistStorage.findById(room.getCode()).get();
		assertThat(savedPlaylist.getVideos()).hasSize(49);
	}

	private Room createRoom(int capacity) {
		Room room = Room.builder()
			.code("room code")
			.title("title")
			.capacity(capacity)
			.createdAt(LocalDateTime.now())
			.password(null)
			.build();
		roomStorage.save(room);

		Playlist playlist = new Playlist(room.getCode());
		playlistStorage.save(playlist);

		return room;
	}

	private void createBulkUsers(int count) {
		for (long i = 0; i < count; i++) {
			User user = User.builder()
				.id(i)
				.nickname("nickname" + i)
				.build();

			userStorage.save(user);
		}
	}

	private void createAndEnterBulkUsers(int count, String roomCode) {
		Room room = roomStorage.findById(roomCode).get();

		for (long i = 0; i < count; i++) {
			User user = User.builder()
				.id(i)
				.nickname("nickname" + i)
				.build();

			user.enterRoom(roomCode);
			user.createRoom(roomCode);        // user 를 호스트로 설정
			room.enter(null);

			userStorage.save(user);
		}
		roomStorage.save(room);
	}

}
