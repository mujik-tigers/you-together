package site.youtogether.user.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.youtogether.room.Participant;
import site.youtogether.user.application.UserService;
import site.youtogether.user.dto.UserNicknameChangeForm;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;
import site.youtogether.util.resolver.UserTracking;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PatchMapping("/users")
	public ResponseEntity<ApiResponse<Participant>> changeUserNickname(@UserTracking Long userId, @RequestBody @Valid UserNicknameChangeForm form) {
		Participant participantInfo = userService.changeUserNickname(userId, form.getNewNickname(), form.getRoomCode());

		return ResponseEntity.ok(ApiResponse.ok(ResponseResult.USER_NICKNAME_CHANGE_SUCCESS, participantInfo));
	}

	@PatchMapping("/users/role")
	public ResponseEntity<ApiResponse<Participant>> changeUserRole(@UserTracking Long userId, @Valid @RequestBody UserRoleChangeForm form) {
		Participant changedParticipantInfo = userService.changeUserRole(userId, form);

		return ResponseEntity.ok(ApiResponse.ok(ResponseResult.USER_ROLE_CHANGE_SUCCESS, changedParticipantInfo));
	}

}
