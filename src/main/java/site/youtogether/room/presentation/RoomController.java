package site.youtogether.room.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.resolver.Address;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.util.RandomUtil;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final CookieProperties cookieProperties;
	private final RoomService roomService;

	@PostMapping("/rooms")
	public ResponseEntity<ApiResponse<RoomCode>> createRoom(@CookieValue(value = SESSION_COOKIE_NAME, required = false) Cookie sessionCookie,
		@Address String address, @Valid @RequestBody RoomSettings roomSettings, HttpServletResponse response) {
		// Check if a session cookie already exists.
		if (sessionCookie != null) {
			throw new SingleRoomParticipationViolationException();
		}

		// Generate a new session code and set it as a cookie.
		ResponseCookie cookie = ResponseCookie.from(cookieProperties.getName(), RandomUtil.generateRandomCode(SESSION_CODE_LENGTH))
			.domain(cookieProperties.getDomain())
			.path(cookieProperties.getPath())
			.sameSite(cookieProperties.getSameSite())
			.maxAge(cookieProperties.getExpiry())
			.httpOnly(true)
			.secure(true)
			.build();

		// Create a new room with the generated session code.
		RoomCode roomCode = roomService.create(cookie.getValue(), address, roomSettings);

		// Add the cookie to the response header.
		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		// Return a response indicating successful room creation.
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.created(ResponseResult.ROOM_CREATION_SUCCESS, roomCode));
	}

	@DeleteMapping("/rooms/{roomCode}/users")
	public ResponseEntity<ApiResponse<Void>> leaveRoom(@CookieValue(value = SESSION_COOKIE_NAME) Cookie sessionCookie,
		@PathVariable String roomCode, HttpServletResponse response) {
		// Leave the room and delete the session.
		roomService.leave(roomCode, sessionCookie.getValue());

		// Expire the cookie.
		ResponseCookie cookie = ResponseCookie.from(cookieProperties.getName(), sessionCookie.getValue())
			.domain(cookieProperties.getDomain())
			.path(cookieProperties.getPath())
			.sameSite(cookieProperties.getSameSite())
			.maxAge(0)
			.httpOnly(true)
			.secure(true)
			.build();

		// Add the cookie to the response header.
		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok()
			.body(ApiResponse.ok(ResponseResult.ROOM_LEAVE_SUCCESS, null));
	}

}
