package site.youtogether.util;

import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
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
	private final PlaylistStorage playlistStorage;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		roomStorage.deleteAll();
		userStorage.deleteAll();
		playlistStorage.deleteAll();

		for (long i = 0; i < NO_PASSWORD_ROOM_COUNT; i++) {
			User host = User.builder()
				.id(i)
				.nickname("황똥땡" + i)
				.build();

			LocalDateTime createTime = LocalDateTime.of(2024, 4, 6, 12, 0, (int)i);
			Room room = Room.builder()
				.code(RandomUtil.generateRandomCode(ROOM_CODE_LENGTH))
				.capacity(5)
				.title("황똥땡의 공개방... no." + i)
				.createdAt(createTime)
				.build();
			roomStorage.save(room);

			Playlist playlist = new Playlist(room.getCode());
			playlistStorage.save(playlist);
		}

		for (long i = 0; i < PASSWORD_ROOM_COUNT; i++) {
			User host = User.builder()
				.id(NO_PASSWORD_ROOM_COUNT + i)
				.nickname("연똥땡" + i)
				.build();

			LocalDateTime createTime = LocalDateTime.of(2024, 4, 6, 11, 0, (int)i);
			Room room = Room.builder()
				.code(RandomUtil.generateRandomCode(ROOM_CODE_LENGTH))
				.capacity(5)
				.title("연똥땡의 은밀한 방... no." + i)
				.createdAt(createTime)
				.password("1234")
				.build();
			roomStorage.save(room);

			Playlist playlist = new Playlist(room.getCode());
			playlistStorage.save(playlist);
		}
	}

}
