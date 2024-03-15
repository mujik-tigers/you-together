package site.youtogether.room.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.infrastructure.RedisStorage;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RedisStorage redisStorage;
	private final RoomStorage roomStorage;
	private final UserStorage userStorage;

	public RoomCode create(String sessionCode, RoomSettings roomSettings) {
		if (redisStorage.isParticipant(sessionCode)) {
			throw new SingleRoomParticipationViolationException();
		}

		User user = userStorage.findById(sessionCode).orElseThrow();
		User host = User.builder()
			.sessionCode(user.getSessionCode())
			.address(user.getAddress())
			.nickname(RandomUtil.generateUserNickname())
			.role(Role.HOST)
			.build();

		Room room = Room.builder()
			.capacity(roomSettings.getCapacity())
			.title(roomSettings.getTitle())
			.password(roomSettings.getPassword())
			.host(host)
			.build();

		userStorage.save(host);
		roomStorage.save(room);
		redisStorage.addParticipant(sessionCode);

		return new RoomCode(room);
	}

}
