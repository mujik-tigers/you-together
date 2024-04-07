package site.youtogether.room.presentation;

import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.dto.CreatedRoomInfo;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.user.application.UserService;
import site.youtogether.util.RandomUtil;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final CookieProperties cookieProperties;
	private final RoomService roomService;
	private final UserService userService;

	@PostMapping("/rooms")
	public ResponseEntity<ApiResponse<CreatedRoomInfo>> createRoom(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionCode,
		@Valid @RequestBody RoomSettings roomSettings, HttpServletResponse response) {
		// Check if a session cookie already exists.
		if (sessionCode != null && userService.isValidSession(sessionCode)) {
			throw new SingleRoomParticipationViolationException();
		}

		// Generate a new session code and set it as a cookie.
		ResponseCookie cookie = ResponseCookie.from(cookieProperties.getName(), RandomUtil.generateRandomCode(COOKIE_VALUE_LENGTH))
			.domain(cookieProperties.getDomain())
			.path(cookieProperties.getPath())
			.sameSite(cookieProperties.getSameSite())
			.maxAge(cookieProperties.getExpiry())
			.httpOnly(true)
			.secure(true)
			.build();

		// Create a new room with the generated session code.
		CreatedRoomInfo createdRoomInfo = roomService.create(cookie.getValue(), roomSettings, LocalDateTime.now());

		// Add the cookie to the response header.
		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		// Return a response indicating successful room creation.
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.created(ResponseResult.ROOM_CREATION_SUCCESS, createdRoomInfo));
	}

	@GetMapping("/rooms")
	public ResponseEntity<ApiResponse<RoomList>> fetchRoomList(@PageableDefault Pageable pageable, @RequestParam(required = false) String keyword) {
		RoomList roomList = roomService.fetchAll(pageable, keyword);

		return ResponseEntity.ok()
			.body(ApiResponse.ok(ResponseResult.ROOM_LIST_FETCH_SUCCESS, roomList));
	}

}
