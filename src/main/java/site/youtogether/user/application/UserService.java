package site.youtogether.user.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.user.infrastructure.UserStorage;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserStorage userStorage;

	public boolean isSessionValid(String sessionCode) {
		return userStorage.existsById(sessionCode);
	}

}
