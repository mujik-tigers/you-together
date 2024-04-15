package site.youtogether.user.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.youtogether.user.application.UserService;
import site.youtogether.user.dto.UpdateUserForm;
import site.youtogether.user.dto.UserInfo;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;
import site.youtogether.util.resolver.UserTracking;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PatchMapping("/users")
	public ResponseEntity<ApiResponse<UserInfo>> updateUserNickname(@UserTracking Long userId, @RequestBody @Valid UpdateUserForm form) {
		UserInfo userInfo = userService.updateUserNickname(userId, form.getUpdateNickname(), form.getRoomCode());

		return ResponseEntity.ok(ApiResponse.ok(ResponseResult.USER_NICKNAME_UPDATE_SUCCESS, userInfo));
	}

	@PatchMapping("/users/role")
	public ResponseEntity<ApiResponse<UserInfo>> changeUserRole(@UserTracking Long userId, @Valid @RequestBody UserRoleChangeForm form) {
		UserInfo changedUserInfo = userService.changeUserRole(userId, form);

		return ResponseEntity.ok(ApiResponse.ok(ResponseResult.USER_ROLE_CHANGE_SUCCESS, changedUserInfo));
	}

}
