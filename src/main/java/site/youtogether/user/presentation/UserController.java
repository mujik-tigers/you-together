package site.youtogether.user.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.user.dto.UserNickname;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserStorage userStorage;

	@GetMapping("/users/nickname")
	public ResponseEntity<ApiResponse<UserNickname>> fetchUserNickname(@CookieValue(value = SESSION_COOKIE_NAME) String sessionCode) {
		UserNickname userNickname = userStorage.findById(sessionCode)
			.map(UserNickname::new)
			.orElseThrow(UserNoExistenceException::new);

		return ResponseEntity.ok(ApiResponse.ok(ResponseResult.USER_NICKNAME_FETCH_SUCCESS, userNickname));
	}

}
