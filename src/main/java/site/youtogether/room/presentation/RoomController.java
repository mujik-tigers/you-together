package site.youtogether.room.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.youtogether.room.Room;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.dto.RoomInfo;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@GetMapping("/rooms/{roomId}")
	public String enterRoom(@PathVariable String roomId, HttpServletRequest request) {
		roomService.enter(roomId, request.getRemoteAddr());

		return "ok";
	}

	@PostMapping("/rooms")
	public String createRoom(@RequestBody RoomInfo roomInfo, HttpServletRequest request) {    // TODO: 사용자 정보 (이름같은거) 도 받아야 한다
		String roomId = roomService.createRoom(request.getRemoteAddr(), roomInfo.getName(), roomInfo.getTotalCapacity());

		return roomId;
	}

	@GetMapping("/rooms")
	public List<Room> fetchAllRooms() {
		return roomService.fetchAllRooms();
	}

}
