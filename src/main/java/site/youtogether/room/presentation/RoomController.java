package site.youtogether.room.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.youtogether.resolver.SessionCode;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@PostMapping("/rooms")
	public ResponseEntity<ApiResponse<RoomCode>> createRoom(@SessionCode String sessionCode, @Valid @RequestBody RoomSettings roomSettings) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.created(ResponseResult.ROOM_CREATION_SUCCESS, roomService.create(sessionCode, roomSettings)));
	}

}
