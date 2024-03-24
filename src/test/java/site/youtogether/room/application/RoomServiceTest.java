package site.youtogether.room.application;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomCode;
import site.youtogether.room.dto.RoomList;
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
	@DisplayName("방 목록을 슬라이스로 가져온다")
	void fetchAllRooms() throws Exception {
		// given
		List<Room> rooms = generateRooms(25);
		roomStorage.saveAll(rooms);

		PageRequest pageRequest = PageRequest.of(0, 10);

		// when
		RoomList roomList = roomService.fetchAll(pageRequest);

		// then
		assertThat(roomList.getPageNumber()).isEqualTo(0);
		assertThat(roomList.isLast()).isFalse();
		assertThat(roomList.getRooms())
			.extracting("code")
			.containsExactly(
				rooms.get(0).getCode(), rooms.get(1).getCode(), rooms.get(2).getCode(), rooms.get(3).getCode(), rooms.get(4).getCode(),
				rooms.get(5).getCode(), rooms.get(6).getCode(), rooms.get(7).getCode(), rooms.get(8).getCode(), rooms.get(9).getCode()
			);
	}
	
	private List<Room> generateRooms(int count) {
		List<Room> rooms = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			User user = User.builder()
				.sessionCode("adskljf" + i)
				.build();

			Room room = Room.builder()
				.host(user)
				.title(i + "번 방")
				.capacity(10)
				.build();

			rooms.add(room);
		}

		return rooms;
	}

}
