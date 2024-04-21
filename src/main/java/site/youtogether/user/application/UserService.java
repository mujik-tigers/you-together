package site.youtogether.user.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.application.MessageService;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserInfo;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.user.infrastructure.UserStorage;

@Service
@RequiredArgsConstructor
public class UserService {

	private final RoomStorage roomStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;

	public UserInfo updateUserNickname(Long userId, String updateNickname, String roomCode) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);
		room.changeParticipantName(userId, updateNickname);
		roomStorage.save(room);

		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		user.changeNickname(updateNickname);
		userStorage.save(user);

		messageService.sendParticipantsInfo(roomCode);

		return new UserInfo(user);
	}

	public UserInfo changeUserRole(Long userId, UserRoleChangeForm form) {
		Room room = roomStorage.findById(form.getRoomCode())
			.orElseThrow(RoomNoExistenceException::new);
		User changedUser = room.changeParticipantRole(userId, form.getChangedUserId(), form.getChangeUserRole());
		roomStorage.save(room);

		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		user.changeRole(form.getChangeUserRole());
		userStorage.save(user);

		messageService.sendParticipantsInfo(room.getCode());

		return new UserInfo(changedUser);
	}

}
