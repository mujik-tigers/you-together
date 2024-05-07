package site.youtogether.user.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.AlarmMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.room.Participant;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.aop.UserSynchronize;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserStorage userStorage;
	private final MessageService messageService;

	public Participant changeUserNickname(Long userId, String newNickname) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		String previousNickname = user.getNickname();
		user.changeNickname(newNickname);
		userStorage.save(user);

		if (user.isParticipant()) {
			messageService.sendParticipants(user.getCurrentRoomCode());
			messageService.sendAlarm(
				new AlarmMessage(user.getCurrentRoomCode(), "[알림] " + previousNickname + "님이 " + newNickname + "(으)로 닉네임을 변경했습니다."));
		}

		return new Participant(user);
	}

	@UserSynchronize
	public Participant changeUserRole(Long userId, UserRoleChangeForm form) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		User targetUser = userStorage.findById(form.getTargetUserId())
			.orElseThrow(UserNoExistenceException::new);
		user.changeOtherUserRole(targetUser, form.getNewUserRole());
		userStorage.save(targetUser);

		messageService.sendParticipants(user.getCurrentRoomCode());
		messageService.sendAlarm(
			new AlarmMessage(user.getCurrentRoomCode(),
				"[알림] " + targetUser.getNickname() + "님의 역할이 " + form.getNewUserRole().name() + "(으)로 변경되었습니다."));

		return new Participant(targetUser);
	}

}
