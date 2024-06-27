package site.youtogether.room.presentation;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.dto.ChangedRoomTitle;
import site.youtogether.room.dto.NewRoom;
import site.youtogether.room.dto.PasswordInput;
import site.youtogether.room.dto.RoomDetail;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.dto.TitleInput;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;
import site.youtogether.util.resolver.UserTracking;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@GetMapping("/rooms")
	public ResponseEntity<ApiResponse<RoomList>> fetchRoomList(@PageableDefault Pageable pageable, @RequestParam(required = false) String keyword) {
		RoomList roomList = roomService.fetchAll(pageable, keyword);

		return ResponseEntity.ok()
			.body(ApiResponse.ok(ResponseResult.ROOM_LIST_FETCH_SUCCESS, roomList));
	}

	@PostMapping("/rooms")
	public ResponseEntity<ApiResponse<NewRoom>> createRoom(@UserTracking Long userId, @Valid @RequestBody RoomSettings roomSettings) {
		NewRoom newRoom = roomService.create(userId, roomSettings, LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.created(ResponseResult.ROOM_CREATION_SUCCESS, newRoom));
	}

	@PostMapping("/rooms/{roomCode}")
	public ResponseEntity<ApiResponse<RoomDetail>> enterRoom(@PathVariable String roomCode, @UserTracking Long userId,
		@Valid @RequestBody(required = false) PasswordInput form) {

		String passwordInput = form == null ? null : form.getPasswordInput();
		RoomDetail roomDetail = roomService.enter(roomCode, userId, passwordInput);

		return ResponseEntity.ok()
			.body(ApiResponse.ok(ResponseResult.ROOM_ENTER_SUCCESS, roomDetail));
	}

	@PatchMapping("/rooms/title")
	public ResponseEntity<ApiResponse<ChangedRoomTitle>> changeRoomTitle(@UserTracking Long userId, @Valid @RequestBody TitleInput form) {
		ChangedRoomTitle changedRoomTitle = roomService.changeRoomTitle(userId, form.getNewTitle());

		return ResponseEntity.ok()
			.body(ApiResponse.ok(ResponseResult.ROOM_TITLE_CHANGE_SUCCESS, changedRoomTitle));
	}

}
