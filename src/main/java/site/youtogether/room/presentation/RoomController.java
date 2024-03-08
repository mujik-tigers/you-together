package site.youtogether.room.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import site.youtogether.room.dto.RoomSettings;

@RestController
public class RoomController {

	@PostMapping("/room")
	public String createRoom(@Valid @RequestBody RoomSettings roomSettings) {
		return "test success";
	}

}
