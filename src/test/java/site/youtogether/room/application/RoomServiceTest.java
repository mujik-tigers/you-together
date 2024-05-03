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
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

class RoomServiceTest extends IntegrationTestSupport {

	private static final Long HOST_ID = 100L;

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private UserStorage userStorage;

	@AfterEach
	void clean() {
		roomStorage.deleteAll();
		userStorage.deleteAll();
	}

	@Test
	@DisplayName("새로운 방과 해당 방의 HOST를 생성할 수 있다")
	void createSuccess() {
		// given
		RoomSettings roomSettings = RoomSettings.builder()
			.capacity(10)
			.title("재밌는 쇼츠 같이 보기")
			.password(null)
			.build();
		User user = createUser(HOST_ID);

		// when
		RoomDetail createdRoomDetail = roomService.create(user.getId(), roomSettings, LocalDateTime.now());

		// then
		Room room = roomStorage.findById(createdRoomDetail.getRoomCode()).get();
		User savedUser = userStorage.findById(user.getId()).get();

		assertThat(createdRoomDetail.getRoomCode()).hasSize(ROOM_CODE_LENGTH);
		assertThat(createdRoomDetail.getRoomCode()).isEqualTo(room.getCode());
		assertThat(room.getCapacity()).isEqualTo(10);
		assertThat(room.getTitle()).isEqualTo("재밌는 쇼츠 같이 보기");
		assertThat(room.getPassword()).isNull();
		assertThat(savedUser.getId()).isEqualTo(user.getId());
		assertThat(savedUser.getRoleInCurrentRoom()).isEqualTo(Role.HOST);
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
		User user = createUser(3L);

		// when
		roomService.enter(user.getId(), room.getCode(), null);

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		User savedUser = userStorage.findById(user.getId()).get();

		assertThat(savedUser.getId()).isEqualTo(user.getId());
		assertThat(savedUser.getRoleInCurrentRoom()).isEqualTo(Role.GUEST);
	}

	@Test
	@DisplayName("비밀번호가 있는 방에 입장한다")
	void enterPasswordRoom() throws Exception {
		// given
		String password = "myLittleCat";
		Room room = createPasswordRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방", password);
		User user = createUser(3L);

		// when
		roomService.enter(user.getId(), room.getCode(), password);

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		User savedUser = userStorage.findById(user.getId()).get();

		assertThat(savedUser.getId()).isEqualTo(user.getId());
		assertThat(savedUser.getRoleInCurrentRoom()).isEqualTo(Role.GUEST);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = "notMatchPassword")
	@DisplayName("입력한 비밀번호가 없거나 일치하지 않으면 방에 입장할 수 없다")
	void enterPasswordRoomFail(String passwordInput) throws Exception {
		// given
		String password = "myLittleCat";
		Room room = createPasswordRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방", password);
		User user = createUser(3L);

		// when // then
		assertThatThrownBy(() -> roomService.enter(user.getId(), room.getCode(), passwordInput))
			.isInstanceOf(PasswordNotMatchException.class);
	}

	@Test
	@DisplayName("인원이 꽉 찬 방은 입장할 수 없다")
	void enterFullRoomFail() throws Exception {
		// given
		int capacity = 1;
		User host = createUser(HOST_ID);
		Room room = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방", capacity, host);
		User user = createUser(3L);

		// when // then
		assertThatThrownBy(() -> roomService.enter(user.getId(), room.getCode(), null))
			.isInstanceOf(RoomCapacityExceededException.class);
	}

	@Test
	@DisplayName("방을 떠난다")
	void leaveRoom() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "연똥땡의 방");
		User user = createUser(2L);
		roomService.enter(user.getId(), room.getCode(), null);

		// when
		roomService.leave(room.getCode(), user.getId());

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		User savedUser = userStorage.findById(user.getId()).get();

		assertThat(savedUser.getCurrentRoomCode()).isNull();
		assertThat(savedUser.isParticipant()).isFalse();
		assertThat(savedUser.getHistory()).containsKey(savedRoom.getCode());
	}

	@Test
	@DisplayName("방 제목을 바꾼다")
	void changeRoom() throws Exception {
		// given
		User host = createUser(HOST_ID);
		Room room = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방", 5, host);

		String updateTitle = "연똥땡의 방";

		// when
		roomService.changeRoomTitle(HOST_ID, room.getCode(), updateTitle);

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		assertThat(savedRoom.getTitle()).isEqualTo(updateTitle);
	}

	@Test
	@DisplayName("나갔던 방에 재입장해도, 방에서의 권한이 유지된다")
	void leaveAndReEnterRoom() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방");
		User user = createUser(3L);

		// when
		roomService.enter(user.getId(), room.getCode(), null);
		userRoleChange(room.getCode(), user, Role.MANAGER);
		roomService.leave(room.getCode(), user.getId());
		roomService.enter(user.getId(), room.getCode(), null);

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();

		User savedUser = userStorage.findById(user.getId()).get();
		assertThat(savedUser.getRoleInCurrentRoom()).isEqualTo(Role.MANAGER);
	}

	@Test
	@DisplayName("이전 방에서의 권한과 상관없이, 새로운 방에 들어가면 GUEST 역할을 받는다")
	void enterNewRoomRefreshRole() throws Exception {
		// given
		Room room1 = createRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방");
		User user = createUser(3L);

		Room room2 = createRoom(LocalDateTime.of(2024, 4, 10, 12, 37, 0), "연츠비의 방");

		// when
		roomService.enter(user.getId(), room1.getCode(), null);
		userRoleChange(room1.getCode(), user, Role.MANAGER);
		roomService.leave(room1.getCode(), user.getId());
		roomService.enter(user.getId(), room2.getCode(), null);

		// then
		Room savedRoom = roomStorage.findById(room2.getCode()).get();

		User savedUser = userStorage.findById(user.getId()).get();
		assertThat(savedUser.getRoleInCurrentRoom()).isEqualTo(Role.GUEST);
	}

	private void userRoleChange(String roomCode, User user, Role newUserRole) {
		user.getHistory().put(roomCode, newUserRole);
		userStorage.save(user);

		Room room = roomStorage.findById(roomCode).get();
		roomStorage.save(room);
	}

	private User createUser(Long userId) {
		User user = User.builder()
			.id(userId)
			.nickname("황츠비")
			.build();
		userStorage.save(user);
		return user;
	}

	private Room createRoom(LocalDateTime createTime, String title) {
		User host = User.builder()
			.id(HOST_ID)
			.build();

		Room room = Room.builder()
			.code(RandomUtil.generateRandomCode(ROOM_CODE_LENGTH))
			.title(title)
			.createdAt(createTime)
			.capacity(10)
			.build();
		roomStorage.save(room);

		return room;
	}

	private Room createRoom(LocalDateTime createTime, String title, int capacity, User host) {
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);

		host.createRoom(roomCode);
		userStorage.save(host);

		Room room = Room.builder()
			.code(roomCode)
			.title(title)
			.createdAt(createTime)
			.capacity(capacity)
			.build();
		roomStorage.save(room);

		return room;
	}

	private Room createPasswordRoom(LocalDateTime createTime, String title, String password) {
		User user = User.builder()
			.id(HOST_ID)
			.build();

		Room room = Room.builder()
			.code("roomCode")
			.title(title)
			.password(password)
			.createdAt(createTime)
			.capacity(10)
			.build();

		roomStorage.save(room);

		return room;
	}

}
