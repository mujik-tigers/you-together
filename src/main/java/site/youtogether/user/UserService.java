package site.youtogether.user;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.user.dto.UserNickname;
import site.youtogether.user.infrastructure.UserStorage;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserStorage userStorage;

	public UserNickname fetchUserNickname(String sessionCode) {
		return userStorage.findById(sessionCode)
			.map(UserNickname::new)
			.orElseThrow(UserNoExistenceException::new);
	}

	public UserNickname updateUserNickname(String sessionCode, String updateName) {
		User user = userStorage.findById(sessionCode)
			.orElseThrow(UserNoExistenceException::new);
		user.changeNickname(updateName);
		userStorage.save(user);

		return new UserNickname(user);
	}

}
