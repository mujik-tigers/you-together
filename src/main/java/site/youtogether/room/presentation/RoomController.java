package site.youtogether.room.presentation;

import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.dto.CreatedRoomInfo;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.user.infrastructure.UserTrackingStorage;
import site.youtogether.util.RandomUtil;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

	private final CookieProperties cookieProperties;
	private final RoomService roomService;
	private final UserTrackingStorage userTrackingStorage;

	@PostMapping("/rooms")
	public ResponseEntity<ApiResponse<CreatedRoomInfo>> createRoom(@Valid @RequestBody RoomSettings roomSettings, HttpServletResponse response) {
		// Generate a new session code and set it as a cookie.
		ResponseCookie cookie = generateSessionCookie();
		String sessionCode = userTrackingStorage.save(cookie.getValue());

		// Create a new room with the generated session code.
		CreatedRoomInfo createdRoomInfo = roomService.create(sessionCode, roomSettings, LocalDateTime.now());

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

	@PostMapping("/rooms/{roomId}")
	public String enterRoom(@PathVariable String roomId, HttpServletResponse response) {
		ResponseCookie cookie = generateSessionCookie();
		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		log.info("roomId = {}", roomId);

		return "ok";
	}

	private ResponseCookie generateSessionCookie() {
		return ResponseCookie.from(cookieProperties.getName(), RandomUtil.generateRandomCode(COOKIE_VALUE_LENGTH))
			.domain(cookieProperties.getDomain())
			.path(cookieProperties.getPath())
			.sameSite(cookieProperties.getSameSite())
			.maxAge(cookieProperties.getExpiry())
			.httpOnly(true)
			.secure(true)
			.build();
	}

}
