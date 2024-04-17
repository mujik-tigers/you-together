package site.youtogether.room.application;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.exception.room.PasswordNotMatchException;
import site.youtogether.exception.room.RoomCapacityExceededException;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomDetail;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserTrackingStorage;

class RoomServiceTest extends IntegrationTestSupport {

	private static final Long HOST_ID = 100L;

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private UserTrackingStorage userTrackingStorage;

	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	@AfterEach
	void clean() {
		roomStorage.deleteAll();

		redisTemplate.delete(redisTemplate.keys(USER_TRACKING_KEY_PREFIX + "*"));
	}

	@Test
	@DisplayName("새로운 방과 해당 방의 HOST를 생성할 수 있다")
	void createSuccess() {
		// given
		String cookieValue = "7644a835e52e45dfa385";
		RoomSettings roomSettings = RoomSettings.builder()
			.capacity(10)
			.title("재밌는 쇼츠 같이 보기")
			.password(null)
			.build();

		// when
		RoomDetail createdRoomDetail = roomService.create(cookieValue, roomSettings, LocalDateTime.now());

		// then
		Room room = roomStorage.findById(createdRoomDetail.getRoomCode()).get();
		Long userId = userTrackingStorage.findByCookieValue(cookieValue).get();
		User user = room.findParticipantBy(userId);

		assertThat(createdRoomDetail.getRoomCode()).hasSize(ROOM_CODE_LENGTH);
		assertThat(createdRoomDetail.getRoomCode()).isEqualTo(room.getCode());
		assertThat(room.getCapacity()).isEqualTo(10);
		assertThat(room.getTitle()).isEqualTo("재밌는 쇼츠 같이 보기");
		assertThat(room.getPassword()).isNull();
		assertThat(room.getParticipants()).hasSize(1);
		assertThat(user.getUserId()).isEqualTo(userId);
		assertThat(user.getNickname()).isNotBlank();
		assertThat(user.getRole()).isEqualTo(Role.HOST);
		assertThat(userTrackingStorage.exists(cookieValue)).isTrue();
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

	@Test
	@DisplayName("방에 입장 한다")
	void enterRoom() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "연똥땡의 방");
		String cookieValue = "adljfkalskdfj";

		// when
		roomService.enter(cookieValue, room.getCode(), null);

		// then
		Long userId = userTrackingStorage.findByCookieValue(cookieValue).get();
		Room savedRoom = roomStorage.findById(room.getCode()).get();

		assertThat(savedRoom.getParticipants()).containsKey(userId);
	}

	@Test
	@DisplayName("비밀번호가 있는 방에 입장한다")
	void enterPasswordRoom() throws Exception {
		// given
		String password = "myLittleCat";
		String cookieValue = "fd98lls01adafg";
		Room room = createPasswordRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방", password);

		// when
		roomService.enter(cookieValue, room.getCode(), password);

		// then
		Long userId = userTrackingStorage.findByCookieValue(cookieValue).get();
		Room savedRoom = roomStorage.findById(room.getCode()).get();

		assertThat(savedRoom.getParticipants()).containsKey(userId);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = "notMatchPassword")
	@DisplayName("입력한 비밀번호가 없거나 일치하지 않으면 방에 입장할 수 없다")
	void enterPasswordRoomFail(String passwordInput) throws Exception {
		// given
		String password = "myLittleCat";
		String cookieValue = "fd98lls01adafg";
		Room room = createPasswordRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방", password);

		// when // then
		assertThatThrownBy(() -> roomService.enter(cookieValue, room.getCode(), passwordInput))
			.isInstanceOf(PasswordNotMatchException.class);
	}

	@Test
	@DisplayName("인원이 꽉 찬 방은 입장할 수 없다")
	void enterFullRoomFail() throws Exception {
		// given
		int capacity = 1;
		String cookieValue = "fd98lls01adafg";
		Room room = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방", capacity);

		// when // then
		assertThatThrownBy(() -> roomService.enter(cookieValue, room.getCode(), null))
			.isInstanceOf(RoomCapacityExceededException.class);
	}

	@Test
	@DisplayName("방을 떠난다")
	void leaveRoom() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "연똥땡의 방");
		User user = User.builder()
			.userId(2L)
			.nickname("황똥땡")
			.role(Role.GUEST)
			.build();
		room.enterParticipant(user, null);
		roomStorage.save(room);

		// when
		roomService.leave(room.getCode(), user.getUserId(), "daflksdfjl");

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		assertThat(savedRoom.getParticipants()).doesNotContainKey(user.getUserId());
	}

	@Test
	@DisplayName("마지막 참가자가 방을 나가면 방은 없어진다")
	void autoDeleteRoom() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "연똥땡의 방");
		roomStorage.save(room);

		// when
		roomService.leave(room.getCode(), HOST_ID, "sdfalkjasdlkfjla");

		// then
		assertThat(roomStorage.existsById(room.getCode())).isFalse();
	}

	@Test
	@DisplayName("방 제목을 바꾼다")
	void changeRoom() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방");
		roomStorage.save(room);

		String updateTitle = "연똥땡의 방";

		// when
		roomService.changeRoomTitle(HOST_ID, room.getCode(), updateTitle);

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		assertThat(savedRoom.getTitle()).isEqualTo(updateTitle);
	}

	private Room createRoom(LocalDateTime createTime, String title) {
		User user = User.builder()
			.userId(HOST_ID)
			.role(Role.HOST)
			.build();

		Room room = Room.builder()
			.title(title)
			.host(user)
			.createdAt(createTime)
			.capacity(10)
			.build();

		roomStorage.save(room);

		return room;
	}

	private Room createRoom(LocalDateTime createTime, String title, int capacity) {
		User user = User.builder()
			.userId(HOST_ID)
			.role(Role.HOST)
			.build();

		Room room = Room.builder()
			.title(title)
			.host(user)
			.createdAt(createTime)
			.capacity(capacity)
			.build();

		roomStorage.save(room);

		return room;
	}

	private Room createPasswordRoom(LocalDateTime createTime, String title, String password) {
		User user = User.builder()
			.role(Role.HOST)
			.userId(HOST_ID)
			.build();

		Room room = Room.builder()
			.title(title)
			.password(password)
			.host(user)
			.createdAt(createTime)
			.capacity(10)
			.build();

		roomStorage.save(room);

		return room;
	}

}
