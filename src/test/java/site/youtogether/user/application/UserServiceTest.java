package site.youtogether.user.application;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.exception.user.UserNicknameDuplicateException;
import site.youtogether.room.Participant;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.user.infrastructure.UniqueNicknameStorage;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

class UserServiceTest extends IntegrationTestSupport {

	private final Long HOST_ID = 1L;

	@Autowired
	private UserService userService;

	@Autowired
	private UserStorage userStorage;

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private UniqueNicknameStorage uniqueNicknameStorage;

	@AfterEach
	void clean() {
		userStorage.deleteAll();
		roomStorage.deleteAll();
		uniqueNicknameStorage.delete();
	}

	@Test
	@DisplayName("유저의 닉네임을 변경한다")
	void changeUserNickname() {
		// given
		Room room = createRoom();
		User user = createUser(room.getCode());

		room.enter(null);
		roomStorage.save(room);

		String newNickname = "new nickname";

		// when
		Participant participant = userService.changeUserNickname(user.getId(), newNickname);

		// then
		User savedUser = userStorage.findById(user.getId()).get();
		Room savedRoom = roomStorage.findById(room.getCode()).get();

		assertThat(participant.getNickname()).isEqualTo(newNickname);
		assertThat(savedUser.getNickname()).isEqualTo(newNickname);
	}

	@Test
	@DisplayName("다른 유저가 사용하는 닉네임으로 변경할 수 없다")
	void changeUserNicknameFail() throws Exception {
		// given
		User user1 = createUser(1L, "hyun");
		User user2 = createUser(2L, "yeon");

		// when // then
		assertThatThrownBy(() -> userService.changeUserNickname(user2.getId(), user1.getNickname()))
			.isInstanceOf(UserNicknameDuplicateException.class);
	}

	@Test
	@DisplayName("유저의 역할을 변경한다")
	void changeUserRole() {
		// given
		Room room = createRoom();
		User user = createUser(room.getCode());

		room.enter(null);
		roomStorage.save(room);

		UserRoleChangeForm userRoleChangeForm = new UserRoleChangeForm(user.getId(), Role.VIEWER);

		// when
		userService.changeUserRole(HOST_ID, userRoleChangeForm);

		// then
		User savedUser = userStorage.findById(user.getId()).get();
		Room savedRoom = roomStorage.findById(room.getCode()).get();

		assertThat(savedUser.getRoleInCurrentRoom()).isEqualTo(userRoleChangeForm.getNewUserRole());
	}

	private Room createRoom() {
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);

		User host = User.builder()
			.id(HOST_ID)
			.nickname("host user")
			.currentRoomCode(roomCode)
			.build();

		host.createRoom(roomCode);
		userStorage.save(host);

		Room room = Room.builder()
			.code(roomCode)
			.title("room title")
			.createdAt(LocalDateTime.of(2024, 4, 21, 17, 0, 0))
			.capacity(10)
			.build();

		roomStorage.save(room);

		return room;
	}

	private User createUser(String currentRoomCode) {
		User user = User.builder()
			.id(2L)
			.nickname("choco chip")
			.currentRoomCode(currentRoomCode)
			.build();

		user.enterRoom(currentRoomCode);
		userStorage.save(user);

		return user;
	}

	private User createUser(Long userId, String nickname) {
		User user = User.builder()
			.id(userId)
			.nickname(nickname)
			.build();

		userStorage.save(user);
		uniqueNicknameStorage.save(nickname);
		return user;
	}

}
