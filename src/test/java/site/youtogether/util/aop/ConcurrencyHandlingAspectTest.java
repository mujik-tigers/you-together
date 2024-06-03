package site.youtogether.util.aop;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.exception.playlist.InvalidVideoNumberException;
import site.youtogether.message.ChatHistory;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.PlayingDefaultVideo;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.Video;
import site.youtogether.playlist.application.PlaylistService;
import site.youtogether.playlist.dto.PlaylistAddForm;
import site.youtogether.playlist.infrastructure.PlayingVideoStorage;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.room.Room;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.application.UserService;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.user.infrastructure.UserStorage;

class ConcurrencyHandlingAspectTest extends IntegrationTestSupport {

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private PlaylistService playlistService;

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private UserStorage userStorage;

	@Autowired
	private PlaylistStorage playlistStorage;

	@Autowired
	private PlayingVideoStorage playingVideoStorage;

	@Autowired
	private MessageService messageService;

	@Autowired
	private RedisTemplate<String, ChatHistory> redisTemplate;

	@AfterEach
	void clean() {
		roomStorage.deleteAll();
		userStorage.deleteAll();
		playlistStorage.deleteAll();
		redisTemplate.delete(redisTemplate.keys(CHAT_PREFIX + "*"));
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
	@DisplayName("여러 명이 동시에 한 명의 역할을 변경")
	void changeOtherRoleMultiThread() throws Exception {
		Room room = createRoom(10);
		User host = createAndEnterUser(1L, Role.HOST, room.getCode());
		User manager1 = createAndEnterUser(2L, Role.MANAGER, room.getCode());
		User manager2 = createAndEnterUser(3L, Role.MANAGER, room.getCode());
		User manager3 = createAndEnterUser(4L, Role.MANAGER, room.getCode());
		User guest = createAndEnterUser(5L, Role.GUEST, room.getCode());

		ExecutorService executorService = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(4);
		executorService.submit(() -> {
			try {
				UserRoleChangeForm form = new UserRoleChangeForm(guest.getId(), Role.MANAGER);
				userService.changeUserRole(host.getId(), form);
			} finally {
				latch.countDown();
			}
		});
		for (long i = 2; i < 5; i++) {
			long userId = i;
			executorService.submit(() -> {
				try {
					UserRoleChangeForm form = new UserRoleChangeForm(guest.getId(), Role.VIEWER);
					userService.changeUserRole(userId, form);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		User targetUser = userStorage.findById(guest.getId()).get();
		assertThat(targetUser.getRoleInCurrentRoom()).isEqualTo(Role.MANAGER);
	}

	@Test
	@DisplayName("동시에 50명이 플레이리스트에 영상 추가")
	void addPlaylistMultiThread() throws Exception {
		Room room = createRoom(50, 0);
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
		assertThat(savedPlaylist.getVideos()).hasSize(50);
	}

	@Test
	@DisplayName("동시에 50명이 플레이리스트에 영상 제거")
	void deletePlaylistMultiThread() throws Exception {
		int videoCount = 100;
		Room room = createRoom(50, videoCount);
		createAndEnterBulkUsers(50, room.getCode());

		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (long i = 0; i < threadCount; i++) {
			long userId = i;
			long videoNumber = i;
			executorService.submit(() -> {
				try {
					playlistService.deleteVideo(userId, videoNumber);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		Playlist savedPlaylist = playlistStorage.findById(room.getCode()).get();
		assertThat(savedPlaylist.getVideos()).hasSize(videoCount - threadCount);
	}

	@Test
	@DisplayName("동시에 20명이 플레이리스트의 다음 영상 재생")
	void playNextVideoMultiThread() throws Exception {
		int videoCount = 10;
		Room room = createRoom(20, videoCount);
		createAndEnterBulkUsers(20, room.getCode());

		int threadCount = 20;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger exceptionCounter = new AtomicInteger();

		for (long i = 0; i < threadCount; i++) {
			long userId = i;

			executorService.submit(() -> {
				try {
					playlistService.playNextVideo(userId, 0L);
				} catch (InvalidVideoNumberException e) {
					exceptionCounter.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		assertThat(exceptionCounter.get()).isEqualTo(threadCount - 1);
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

	private Room createRoom(int capacity, int videoCount) {
		Room room = Room.builder()
			.code("room code")
			.title("title")
			.capacity(capacity)
			.createdAt(LocalDateTime.now())
			.password(null)
			.build();
		roomStorage.save(room);

		Playlist playlist = new Playlist(room.getCode());
		for (long i = 0; i < videoCount; i++) {
			Video video = Video.builder()
				.videoNumber(i)
				.videoId("videoId" + i)
				.duration(100000)
				.build();
			playlist.add(video);
		}
		playlistStorage.save(playlist);

		Video video = Video.builder()
			.videoNumber(9999L)
			.videoId("videoId" + 9999)
			.duration(100000)
			.build();
		playingVideoStorage.saveAndPlay(new PlayingDefaultVideo(room.getCode(), video, messageService, playlistService));

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

	private User createAndEnterUser(long id, Role role, String roomCode) {
		User user = User.builder()
			.id(id)
			.currentRoomCode(roomCode)
			.build();

		user.getHistory().put(roomCode, role);
		userStorage.save(user);

		Room room = roomStorage.findById(roomCode).get();
		room.enter(null);
		roomStorage.save(room);

		return user;
	}

}
