package site.youtogether.room.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.storage.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.util.RandomUtils;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomStorage roomStorage;

	public RoomCode create(String address, RoomSettings roomSettings) {
		Room room = createRoom(roomSettings);
		User host = createHost(room, address);
		room.addParticipant(address, host);

		roomStorage.save(address, room);

		return new RoomCode(room);
	}

	private Room createRoom(RoomSettings roomSettings) {
		return Room.builder()
			.title(roomSettings.getTitle())
			.capacity(roomSettings.getCapacity())
			.password(roomSettings.getPassword())
			.build();
	}

	private User createHost(Room room, String address) {
		return User.builder()
			.address(address)
			.nickname(RandomUtils.generateUserNickname())
			.role(Role.HOST)
			.build();
	}

}
