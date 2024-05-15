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
import site.youtogether.exception.user.UserNotEnteringException;
import site.youtogether.message.ChatHistory;
import site.youtogether.room.Room;
import site.youtogether.room.dto.NewRoom;
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

	@Autowired
	private RedisTemplate<String, ChatHistory> redisTemplate;

	@AfterEach
	void clean() {
		roomStorage.deleteAll();
		userStorage.deleteAll();
		redisTemplate.delete(redisTemplate.keys(CHAT_PREFIX + "*"));
	}

	@Test
	@DisplayName("새로운 방을 생성한다")
	void createSuccess() {
		// given
		RoomSettings roomSettings = RoomSettings.builder()
			.capacity(10)
			.title("재밌는 쇼츠 같이 보기")
			.password(null)
			.build();
		User user = createUser(HOST_ID);

		// when
		NewRoom newRoom = roomService.create(user.getId(), roomSettings, LocalDateTime.now());

		// then
		Room room = roomStorage.findById(newRoom.getRoomCode()).get();
		User savedUser = userStorage.findById(user.getId()).get();

		assertThat(newRoom.getRoomCode()).hasSize(ROOM_CODE_LENGTH);
		assertThat(newRoom.getRoomCode()).isEqualTo(room.getCode());
		assertThat(room.getCapacity()).isEqualTo(10);
		assertThat(room.getTitle()).isEqualTo("재밌는 쇼츠 같이 보기");
		assertThat(room.getPassword()).isNull();
		assertThat(room.getParticipantCount()).isZero();

		assertThat(savedUser.getId()).isEqualTo(user.getId());
		assertThat(savedUser.isParticipant()).isFalse();
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

	// TODO: 최종 배포 전 주석 해제하기
	// @Test
	// @DisplayName("빈 방은 목록 조회 시 포함하지 않는다")
	// void fetchOnlyActiveRoom() throws Exception {
	// 	// given
	// 	Room room1 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 0), "room title1");
	// 	Room room2 = createRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 0), "room title2");
	// 	Room room3 = createEmptyRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 0), "empty room1");
	// 	Room room4 = createEmptyRoom(LocalDateTime.of(2024, 4, 6, 12, 0, 0), "empty room2");
	//
	// 	PageRequest pageRequest = PageRequest.of(0, 5);
	//
	// 	// when
	// 	RoomList roomList = roomService.fetchAll(pageRequest, null);
	//
	// 	// then
	// 	assertThat(roomList.getRooms()).hasSize(2);
	// }

	@Test
	@DisplayName("방에 입장 한다")
	void enterRoom() throws Exception {
		// given
		Room room = createEmptyRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "연똥땡의 방");
		User user = createUser(3L);

		// when
		roomService.enter(room.getCode(), user.getId(), null);

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		User savedUser = userStorage.findById(user.getId()).get();

		assertThat(savedRoom.getParticipantCount()).isOne();

		assertThat(savedUser.isParticipant()).isTrue();
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
		roomService.enter(room.getCode(), user.getId(), password);

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		User savedUser = userStorage.findById(user.getId()).get();

		assertThat(savedRoom.getParticipantCount()).isOne();

		assertThat(savedUser.isParticipant()).isTrue();
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
		assertThatThrownBy(() -> roomService.enter(room.getCode(), user.getId(), passwordInput))
			.isInstanceOf(PasswordNotMatchException.class);
	}

	@Test
	@DisplayName("인원이 꽉 찬 방은 입장할 수 없다")
	void enterFullRoomFail() throws Exception {
		// given
		int capacity = 1;
		Room room = createEmptyRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방", capacity);
		User participant = createUser(2L);
		roomService.enter(room.getCode(), participant.getId(), null);

		User user = createUser(3L);

		// when // then
		assertThatThrownBy(() -> roomService.enter(room.getCode(), user.getId(), null))
			.isInstanceOf(RoomCapacityExceededException.class);
	}

	@Test
	@DisplayName("방을 떠난다")
	void leaveRoom() throws Exception {
		// given
		Room room = createEmptyRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "연똥땡의 방");
		User user = createUser(2L);
		roomService.enter(room.getCode(), user.getId(), null);

		// when
		roomService.leave(user.getId());

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		User savedUser = userStorage.findById(user.getId()).get();

		assertThat(savedRoom.getParticipantCount()).isZero();

		assertThat(savedUser.isParticipant()).isFalse();
		assertThatThrownBy(() -> savedUser.getCurrentRoomCode())
			.isInstanceOf(UserNotEnteringException.class);
		assertThat(savedUser.getHistory()).containsKey(savedRoom.getCode());
	}

	@Test
	@DisplayName("방 제목을 바꾼다")
	void changeRoom() throws Exception {
		// given
		Room room = createEmptyRoom(LocalDateTime.of(2024, 4, 10, 11, 37, 0), "황똥땡의 방", 5);
		User host = createUser(HOST_ID);
		host.createRoom(room.getCode());
		host.enterRoom(room.getCode());
		userStorage.save(host);

		String updateTitle = "연똥땡의 방";

		// when
		roomService.changeRoomTitle(HOST_ID, updateTitle);

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		assertThat(savedRoom.getTitle()).isEqualTo(updateTitle);
	}

	@Test
	@DisplayName("유저가 입장한 방 기록은 최대 10개만 방문 순서대로 유지된다")
	void userHistory() throws Exception {
		// given
		User user = createUser(1L);

		// when
		for (int i = 0; i < 100; i++) {
			Room room = createRoom(LocalDateTime.now(), "roomCode" + i);
			roomService.enter(room.getCode(), user.getId(), null);
		}

		// then
		User savedUser = userStorage.findById(user.getId()).get();
		assertThat(savedUser.getHistory().size()).isEqualTo(10);
	}

	@Test
	@DisplayName("방을 재입장하는 경우, 방 기록 순서는 갱신된다")
	void userHistoryUpdate() throws Exception {
		// given
		User user = createUser(1L);

		Room firstRoom = createRoom(LocalDateTime.now(), "firstRoomCode");
		roomService.enter(firstRoom.getCode(), user.getId(), null);

		Room room1 = createRoom(LocalDateTime.now(), "room1");
		roomService.enter(room1.getCode(), user.getId(), null);

		Room room2 = createRoom(LocalDateTime.now(), "room2");
		roomService.enter(room2.getCode(), user.getId(), null);

		Room room3 = createRoom(LocalDateTime.now(), "room3");
		roomService.enter(room3.getCode(), user.getId(), null);

		// when
		roomService.enter(firstRoom.getCode(), user.getId(), null);

		// then
		User savedUser = userStorage.findById(user.getId()).get();
		assertThat(savedUser.getHistory().size()).isEqualTo(4);
		assertThat(savedUser.getRoomCodeQueue()).containsExactly(room1.getCode(), room2.getCode(), room3.getCode(), firstRoom.getCode());
	}

	private User createUser(Long userId) {
		User user = User.builder()
			.id(userId)
			.nickname("황츠비")
			.build();
		userStorage.save(user);
		return user;
	}

	private Room createEmptyRoom(LocalDateTime createTime, String title) {
		Room room = Room.builder()
			.code(RandomUtil.generateRandomCode(ROOM_CODE_LENGTH))
			.title(title)
			.createdAt(createTime)
			.capacity(10)
			.build();
		roomStorage.save(room);

		return room;
	}

	private Room createRoom(LocalDateTime createTime, String title) {
		Room room = Room.builder()
			.code(RandomUtil.generateRandomCode(ROOM_CODE_LENGTH))
			.title(title)
			.createdAt(createTime)
			.capacity(10)
			.build();
		room.enter(null);
		roomStorage.save(room);

		return room;
	}

	private Room createEmptyRoom(LocalDateTime createTime, String title, int capacity) {
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
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
