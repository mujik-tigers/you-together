package site.youtogether.room.application;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.exception.ErrorType;
import site.youtogether.exception.room.UserNotInRoomException;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

class RoomServiceTest extends IntegrationTestSupport {

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private UserStorage userStorage;

	@BeforeEach
	void clean() {
		roomStorage.deleteAll();
		userStorage.deleteAll();
	}

	@Test
	@DisplayName("새로운 방과 해당 방의 HOST를 생성할 수 있다")
	void createSuccess() {
		// given
		String sessionCode = "7644a835e52e45dfa385";
		String address = "127.0.0.1";
		RoomSettings roomSettings = RoomSettings.builder()
			.capacity(10)
			.title("재밌는 쇼츠 같이 보기")
			.password(null)
			.build();

		// when
		RoomCode roomCode = roomService.create(sessionCode, address, roomSettings);

		// then
		Room room = roomStorage.findById(roomCode.getRoomCode()).get();
		User user = userStorage.findById(sessionCode).get();

		assertThat(roomCode.getRoomCode()).hasSize(ROOM_CODE_LENGTH);
		assertThat(roomCode.getRoomCode()).isEqualTo(room.getCode());
		assertThat(room.getCapacity()).isEqualTo(10);
		assertThat(room.getTitle()).isEqualTo("재밌는 쇼츠 같이 보기");
		assertThat(room.getPassword()).isNull();
		assertThat(room.getHost().getSessionCode()).isEqualTo(sessionCode);
		assertThat(room.getParticipants()).hasSize(1);
		assertThat(user.getSessionCode()).isEqualTo(sessionCode);
		assertThat(user.getAddress()).isEqualTo(address);
		assertThat(user.getNickname()).isNotBlank();
		assertThat(user.getRole()).isEqualTo(Role.HOST);
	}

	@Test
	@DisplayName("방에서 나가면 세션이 종료된다")
	void leaveSuccess() {
		// given
		Room room = createRoom();    // Creating a room with a host
		User guest = User.builder()    // Creating a guest and entering the room
			.sessionCode("1644a835e52e45dfa381")
			.role(Role.GUEST)
			.build();
		room.enter(guest);

		roomStorage.save(room);    // Saving to the database
		userStorage.save(guest);

		// when
		roomService.leave(room.getCode(), guest.getSessionCode());    // The entered guest leaves the room

		// then
		Room leftRoom = roomStorage.findById(room.getCode()).get();

		assertThat(leftRoom.getParticipants()).hasSize(1);
		assertThat(leftRoom.getParticipants()).doesNotContainKey(guest.getSessionCode());
		assertThat(userStorage.existsById(guest.getSessionCode())).isFalse();
	}

	@Test
	@DisplayName("방 참여자가 아닌 사용자가 나가려고 할 때 예외가 발생한다")
	void leaveFail_UserNotInRoom() {
		// given
		Room room = createRoom();
		roomStorage.save(room);

		// when / then
		assertThatThrownBy(() -> roomService.leave(room.getCode(), "1644a835e52e45dfa381"))
			.isInstanceOf(UserNotInRoomException.class)
			.hasMessage(ErrorType.USER_NOT_IN_ROOM.getMessage());
	}

	private Room createRoom() {
		User host = User.builder()
			.sessionCode("7644a835e52e45dfa385")
			.role(Role.HOST)
			.build();

		Room room = Room.builder()
			.capacity(10)
			.title("재밌는 쇼츠 같이 보기")
			.password(null)
			.host(host)
			.build();

		room.enter(host);

		return room;
	}

}
