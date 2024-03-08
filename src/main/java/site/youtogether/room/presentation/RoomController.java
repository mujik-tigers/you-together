package site.youtogether.room.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.youtogether.resolver.Address;
import site.youtogether.room.application.RoomService;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomSettings;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@PostMapping("/rooms")
	public RoomCode createRoom(@Address String address, @Valid @RequestBody RoomSettings roomSettings) {
		return roomService.create(address, roomSettings);
	}

}
