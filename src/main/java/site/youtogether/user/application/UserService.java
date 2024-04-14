package site.youtogether.user.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.message.application.RedisSubscriber;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserInfo;
import site.youtogether.user.dto.UserRoleChangeForm;

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

	public UserInfo changeUserRole(Long userId, UserRoleChangeForm form) {
		Room room = roomStorage.findById(form.getRoomCode())
			.orElseThrow(RoomNoExistenceException::new);
		User changedUser = room.changeParticipantRole(userId, form.getChangedUserId(), form.getChangeUserRole());
		roomStorage.save(room);

		redisSubscriber.sendParticipantsInfo(room.getCode());

		return new UserInfo(changedUser);
	}

}
