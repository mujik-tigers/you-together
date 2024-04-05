package site.youtogether.util;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class Initializer implements ApplicationRunner {

	private static final int NO_PASSWORD_ROOM_COUNT = 50;
	private static final int PASSWORD_ROOM_COUNT = 55;

	private final RoomStorage roomStorage;
	private final UserStorage userStorage;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		roomStorage.deleteAll();
		userStorage.deleteAll();

		for (int i = 0; i < NO_PASSWORD_ROOM_COUNT; i++) {
			User host = new User("gahldsk" + i, "127.0.10." + i, "황똥땡" + i, Role.HOST);

			Room room = Room.builder()
				.host(host)
				.capacity(5)
				.title("황똥땡의 공개방... no." + i)
				.build();
			roomStorage.save(room);
		}

		for (int i = 0; i < PASSWORD_ROOM_COUNT; i++) {
			User host = new User("asdjkfh" + i, "127.0.11." + i, "시크릿똥땡" + i, Role.HOST);

			Room room = Room.builder()
				.host(host)
				.capacity(5)
				.title("황똥땡의 은밀한 방... no." + i)
				.password("1234")
				.build();
			roomStorage.save(room);
		}
	}

}
