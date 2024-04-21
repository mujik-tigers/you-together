package site.youtogether.user.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.application.MessageService;
import site.youtogether.room.Participant;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.user.infrastructure.UserStorage;

@Service
@RequiredArgsConstructor
public class UserService {

	private final RoomStorage roomStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;

	public Participant changeUserNickname(Long userId, String newNickname, String roomCode) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		user.changeNickname(newNickname);
		userStorage.save(user);

		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);
		room.updateParticipant(user);
		roomStorage.save(room);

		messageService.sendParticipants(roomCode);

		return new Participant(user);
	}

	public Participant changeUserRole(Long userId, UserRoleChangeForm form) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		User targetUser = userStorage.findById(form.getTargetUserId())
			.orElseThrow(UserNoExistenceException::new);
		user.changeOtherUserRole(form.getRoomCode(), targetUser, form.getNewUserRole());
		userStorage.save(targetUser);

		Room room = roomStorage.findById(form.getRoomCode())
			.orElseThrow(RoomNoExistenceException::new);
		room.updateParticipant(targetUser);
		roomStorage.save(room);

		messageService.sendParticipants(room.getCode());

		return new Participant(targetUser);
	}

}
