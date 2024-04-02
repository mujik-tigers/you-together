package site.youtogether.user.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.user.application.UserService;
import site.youtogether.user.dto.UserInfo;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/users")
	public ResponseEntity<ApiResponse<UserInfo>> fetchUserInfo(@CookieValue(value = SESSION_COOKIE_NAME) String sessionCode) {
		UserInfo userInfo = userService.fetchUserInfo(sessionCode);

		return ResponseEntity.ok(ApiResponse.ok(ResponseResult.USER_INFO_FETCH_SUCCESS, userInfo));
	}

}
