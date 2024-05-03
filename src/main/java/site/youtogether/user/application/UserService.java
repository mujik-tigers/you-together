package site.youtogether.user.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.application.MessageService;
import site.youtogether.room.Participant;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.user.infrastructure.UserStorage;

@Service
@RequiredArgsConstructor
public class UserService {

	private final RoomStorage roomStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;

	public Participant changeUserNickname(Long userId, String newNickname) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		user.changeNickname(newNickname);
		userStorage.save(user);

		if (user.isParticipant()) {
			messageService.sendParticipants(user.getCurrentRoomCode());
		}

		return new Participant(user);
	}

	public Participant changeUserRole(Long userId, UserRoleChangeForm form) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		User targetUser = userStorage.findById(form.getTargetUserId())
			.orElseThrow(UserNoExistenceException::new);
		user.changeOtherUserRole(targetUser, form.getNewUserRole());
		userStorage.save(targetUser);

		messageService.sendParticipants(user.getCurrentRoomCode());

		return new Participant(targetUser);
	}

}
