package site.youtogether.user.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.youtogether.user.UserService;
import site.youtogether.user.dto.UpdateNicknameForm;
import site.youtogether.user.dto.UserNickname;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/user/nickname")
	public ResponseEntity<ApiResponse<UserNickname>> fetchUsername(@CookieValue(value = SESSION_COOKIE_NAME) String sessionCode) {
		UserNickname userNickname = userService.fetchUserNickname(sessionCode);

		return ResponseEntity.ok(ApiResponse.ok(ResponseResult.USER_NICKNAME_FETCH_SUCCESS, userNickname));
	}

	@PatchMapping("/user/nickname")
	public ResponseEntity<ApiResponse<UserNickname>> updateUsername(@CookieValue(value = SESSION_COOKIE_NAME) String sessionCode,
		@RequestBody @Valid UpdateNicknameForm form) {

		UserNickname userNickname = userService.updateUserNickname(sessionCode, form.getUpdateName());

		return ResponseEntity.ok(ApiResponse.ok(ResponseResult.USER_NICKNAME_UPDATE_SUCCESS, userNickname));
	}

}
