package site.youtogether.room.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

public class RoomServiceThreadTest extends IntegrationTestSupport {

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private UserStorage userStorage;

	@BeforeEach
	void setUp() {
		Room room = Room.builder()
			.code("room code")
			.title("title")
			.capacity(100)
			.createdAt(LocalDateTime.now())
			.password(null)
			.build();

		roomStorage.save(room);

		for (long i = 0; i < 150; i++) {
			User user = User.builder()
				.id(i)
				.nickname("nickname" + i)
				.build();

			userStorage.save(user);
		}
	}

	@AfterEach
	void clean() {
		roomStorage.deleteAll();
		userStorage.deleteAll();
	}

	@Test
	@DisplayName("동시에 100명 입장")
	void testMultiThread() throws InterruptedException {
		int threadCount = 150;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (long i = 0; i < threadCount; i++) {
			long userId = i;
			executorService.submit(() -> {
				try {
					roomService.enterWithLock(userId, "room code", null);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Room room = roomStorage.findById("room code").get();

		assertThat(room.getParticipantCount()).isEqualTo(100);
		// Expected: 100 but Actual: ?
	}

	@Test
	@DisplayName("")
	void test() {
		roomService.enterWithLock(0L, "room code", null);
	}

}
