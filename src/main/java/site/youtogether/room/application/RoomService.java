package site.youtogether.room.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNotFoundException;
import site.youtogether.exception.room.UserNotInRoomException;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomStorage roomStorage;
	private final UserStorage userStorage;

	public RoomCode create(String sessionCode, String address, RoomSettings roomSettings) {
		User host = User.builder()
			.sessionCode(sessionCode)
			.address(address)
			.nickname(RandomUtil.generateUserNickname())
			.role(Role.HOST)
			.build();

		Room room = Room.builder()
			.capacity(roomSettings.getCapacity())
			.title(roomSettings.getTitle())
			.password(roomSettings.getPassword())
			.host(host)
			.build();

		userStorage.save(host);
		roomStorage.save(room);

		return new RoomCode(room);
	}

	public void leave(String roomCode, String sessionCode) {
		Room room = roomStorage.findById(roomCode).orElseThrow(RoomNotFoundException::new);

		if (room.getHost().getSessionCode().equals(sessionCode)) {    // Check if the user leaving the room is the host.
			for (String userSession : room.getParticipants().keySet()) {    // Expiring the sessions of all participants.
				userStorage.deleteById(userSession);
			}

			roomStorage.deleteById(roomCode);    // Close the room.
			return;
		}

		User leftUser = room.leave(sessionCode);

		if (leftUser == null) {
			throw new UserNotInRoomException();
		}

		roomStorage.save(room);
		userStorage.deleteById(sessionCode);
	}

}
