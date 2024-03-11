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

	public RoomCode create(String address, RoomSettings roomSettings) {
		if (redisStorage.existsInActiveAddress(address)) {
			throw new SingleRoomParticipationViolationException();
		}

		Room room = Room.builder()
			.title(roomSettings.getTitle())
			.capacity(roomSettings.getCapacity())
			.password(roomSettings.getPassword())
			.build();

		User host = User.builder()
			.address(address)
			.nickname(RandomUtil.generateUserNickname())
			.role(Role.HOST)
			.build();

		roomStorage.save(room);
		userStorage.save(host);

		redisStorage.addActiveAddress(address);
		redisStorage.addParticipant(room.getCode(), address);

		return new RoomCode(room);
	}

}
