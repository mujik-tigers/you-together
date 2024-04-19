package site.youtogether.room.presentation;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.youtogether.jwt.JwtService;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.dto.PasswordInput;
import site.youtogether.room.dto.RoomDetail;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.dto.RoomTitleChangeForm;
import site.youtogether.room.dto.UpdatedRoomTitle;
import site.youtogether.util.RandomUtil;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;
import site.youtogether.util.resolver.UserTracking;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;
	private final JwtService jwtService;
	private final HttpServletResponse response;

	@PostMapping("/rooms")
	public ResponseEntity<ApiResponse<RoomDetail>> createRoom(@Valid @RequestBody RoomSettings roomSettings) {
		Long userId = RandomUtil.generateUserId();
		RoomDetail roomDetail = roomService.create(userId, roomSettings, LocalDateTime.now());

		String token = jwtService.issue(userId, Duration.ofDays(1));
		response.setHeader(HttpHeaders.AUTHORIZATION, token);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.created(ResponseResult.ROOM_CREATION_SUCCESS, roomDetail));
	}

	@GetMapping("/rooms")
	public ResponseEntity<ApiResponse<RoomList>> fetchRoomList(@PageableDefault Pageable pageable, @RequestParam(required = false) String keyword) {
		RoomList roomList = roomService.fetchAll(pageable, keyword);

		return ResponseEntity.ok()
			.body(ApiResponse.ok(ResponseResult.ROOM_LIST_FETCH_SUCCESS, roomList));
	}

	@PostMapping("/rooms/{roomCode}")
	public ResponseEntity<ApiResponse<RoomDetail>> enterRoom(@PathVariable String roomCode,
		@Valid @RequestBody(required = false) PasswordInput form) {

		Long userId = RandomUtil.generateUserId();
		String passwordInput = form == null ? null : form.getPasswordInput();
		RoomDetail roomDetail = roomService.enter(userId, roomCode, passwordInput);

		String token = jwtService.issue(userId, Duration.ofDays(1));
		response.setHeader(HttpHeaders.AUTHORIZATION, token);
		
		return ResponseEntity.ok(
			ApiResponse.ok(ResponseResult.ROOM_ENTER_SUCCESS, roomDetail));
	}

	@PatchMapping("/rooms/title")
	public ResponseEntity<ApiResponse<UpdatedRoomTitle>> changeRoomTitle(@UserTracking Long userId, @Valid @RequestBody RoomTitleChangeForm form) {
		UpdatedRoomTitle updatedRoomTitle = roomService.changeRoomTitle(userId, form.getRoomCode(), form.getUpdateTitle());

		return ResponseEntity.ok(
			ApiResponse.ok(ResponseResult.ROOM_TITLE_CHANGE_SUCCESS, updatedRoomTitle));
	}

}
