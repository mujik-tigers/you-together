package site.youtogether.room.application;

import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.ErrorType;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomInfo;
import site.youtogether.room.dto.RoomList;
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

	public RoomCode enter(String roomCode, String sessionCode, String address) {    // TODO: 이 부분 하나의 트랜잭션 처리 해줘야 할듯, ex) lock
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		User user = User.builder()
			.sessionCode(sessionCode)
			.address(address)
			.nickname(RandomUtil.generateUserNickname())
			.role(Role.GUEST)
			.build();
		userStorage.save(user);

		room.enter(user);
		roomStorage.save(room);

		return new RoomCode(room);
	}

	public RoomList fetchAll(Pageable pageable) {
		Slice<RoomInfo> roomInfoSlice = roomStorage.findAll(pageable)
			.map(RoomInfo::new);

		return new RoomList(roomInfoSlice);
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
