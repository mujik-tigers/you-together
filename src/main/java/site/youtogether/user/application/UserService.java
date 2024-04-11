package site.youtogether.user.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.application.RedisSubscriber;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserInfo;
import site.youtogether.user.infrastructure.UserStorage;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserStorage userStorage;
	private final RoomStorage roomStorage;
	private final RedisSubscriber redisSubscriber;

	public UserInfo updateUserNickname(Long userId, String updateNickname, String roomCode) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		user.changeNickname(updateNickname);
		userStorage.save(user);

		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);
		room.changeParticipantName(userId, updateNickname);
		roomStorage.save(room);

		redisSubscriber.sendParticipantsInfo(roomCode);

		return new UserInfo(user);
	}

}
