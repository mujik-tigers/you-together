package site.youtogether.room.application;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.room.Room;
import site.youtogether.room.dto.CreatedRoomInfo;
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
		CreatedRoomInfo createdRoomInfo = roomService.create(sessionCode, roomSettings, LocalDateTime.now());

		// then
		Room room = roomStorage.findById(createdRoomInfo.getRoomCode()).get();
		User user = userStorage.findById(sessionCode).get();

		assertThat(createdRoomInfo.getRoomCode()).hasSize(ROOM_CODE_LENGTH);
		assertThat(createdRoomInfo.getRoomCode()).isEqualTo(room.getCode());
		assertThat(room.getCapacity()).isEqualTo(10);
		assertThat(room.getTitle()).isEqualTo("재밌는 쇼츠 같이 보기");
		assertThat(room.getPassword()).isNull();
		assertThat(room.getHost().getSessionCode()).isEqualTo(sessionCode);
		assertThat(room.getParticipants()).hasSize(1);
		assertThat(user.getSessionCode()).isEqualTo(sessionCode);
		assertThat(user.getNickname()).isNotBlank();
		assertThat(user.getRole()).isEqualTo(Role.HOST);
	}

	@Test
	@DisplayName("방 목록을 조회할 수 있다")
	void fetchRoomSlice() throws Exception {
		// given
		Room room1 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 0), "가똥댕의 방");
		Room room2 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 1), "나똥댕의 방");
		Room room3 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 2), "다똥댕의 방");
		Room room4 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 3), "라똥댕의 방");
		Room room5 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 4), "마똥댕의 방");
		Room room6 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 5), "바똥댕의 방");
		Room room7 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 6), "사똥댕의 방");
		Room room8 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 7), "아똥댕의 방");
		Room room9 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 8), "자똥댕의 방");
		Room room10 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 9), "차똥댕의 방");

		PageRequest pageRequest = PageRequest.of(0, 3);

		// when
		RoomList roomList1 = roomService.fetchAll(pageRequest, null);
		RoomList roomList2 = roomService.fetchAll(pageRequest, "");
		RoomList roomList3 = roomService.fetchAll(pageRequest, "바 ");

		// then
		assertThat(roomList1.getRooms()).extracting("roomTitle").containsExactly(
			room10.getTitle(), room9.getTitle(), room8.getTitle()
		);
		assertThat(roomList1.isHasNext()).isTrue();

		assertThat(roomList2.getRooms()).extracting("roomTitle").containsExactly(
			room10.getTitle(), room9.getTitle(), room8.getTitle()
		);
		assertThat(roomList2.isHasNext()).isTrue();

		assertThat(roomList3.getRooms()).extracting("roomTitle").containsExactly("바똥댕의 방");
		assertThat(roomList3.isHasNext()).isFalse();
	}

	private Room createRoom(LocalDateTime createTime, String title) {
		User user = User.builder()
			.sessionCode("dakfhsldjk")
			.build();

		Room room = Room.builder()
			.title(title)
			.host(user)
			.createdAt(createTime)
			.build();

		roomStorage.save(room);

		return room;
	}

}
