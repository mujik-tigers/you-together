package site.youtogether.room.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.dto.RoomCreateForm;
import site.youtogether.room.dto.RoomId;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@PostMapping("/rooms")
	public RoomId createRoom(HttpServletRequest request, @RequestBody RoomCreateForm roomCreateForm) {
		String roomId = roomService.createRoom(roomCreateForm.getName(),
			roomCreateForm.getPassword(),
			roomCreateForm.getTotalCapacity(),
			request.getRemoteAddr());

		return new RoomId(roomId);
	}

}
