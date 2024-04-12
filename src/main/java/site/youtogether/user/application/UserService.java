package site.youtogether.user.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.message.application.RedisSubscriber;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserInfo;

@Service
@RequiredArgsConstructor
public class UserService {

	private final RoomStorage roomStorage;
	private final RedisSubscriber redisSubscriber;

	public UserInfo updateUserNickname(Long userId, String updateNickname, String roomCode) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);
		User user = room.changeParticipantName(userId, updateNickname);
		roomStorage.save(room);

		redisSubscriber.sendParticipantsInfo(roomCode);

		return new UserInfo(user);
	}

}
