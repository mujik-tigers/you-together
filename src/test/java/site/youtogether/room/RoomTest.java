package site.youtogether.room;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import site.youtogether.exception.room.PasswordNotMatchException;
import site.youtogether.exception.room.RoomCapacityExceededException;
import site.youtogether.exception.user.ChangeRoomTitleDeniedException;
import site.youtogether.user.User;
import site.youtogether.util.RandomUtil;

class RoomTest {

	@Test
	@DisplayName("HOST는 현재 참여 중인 방의 제목을 변경할 수 있다")
	void changeRoomTitleSuccess() {
		// given
		User host = createUser(1L);
		Room room = createRoom(host, null, 5);
		host.enterRoom(room.getCode());
		room.enter(null);

		String originTitle = room.getTitle();
		String newTitle = "new title";

		// when
		room.changeTitle(host, newTitle);

		// then
		assertThat(room.getTitle()).isNotEqualTo(originTitle);
		assertThat(room.getTitle()).isEqualTo(newTitle);
	}

	@Test
	@DisplayName("HOST가 아닌 유저는 방 제목을 변경할 수 없다")
	void changeRoomTitleFail() {
		// given
		User host = createUser(1L);
		Room room = createRoom(host, null, 5);

		User user = createUser(2L);
		user.enterRoom(room.getCode());
		room.enter(null);

		String newTitle = "new title";

		// when // then
		assertThatThrownBy(() -> room.changeTitle(user, newTitle))
			.isInstanceOf(ChangeRoomTitleDeniedException.class);
	}

	@Test
	@DisplayName("비밀번호가 일치하면 방에 입장할 수 있다")
	void enterPasswordSuccess() {
		// given
		User host = createUser(1L);

		String password = "room password";
		Room room = createRoom(host, password, 5);

		// when
		room.enter(password);

		// then
		assertThat(room.getParticipantCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("비밀번호가 일치하지 않으면 방에 입장할 수 없다")
	void enterPasswordFail() {
		// given
		User host = createUser(1L);

		String password = "room password";
		Room room = createRoom(host, "wrong password", 5);

		// when / then
		assertThatThrownBy(() -> room.enter(password))
			.isInstanceOf(PasswordNotMatchException.class);
	}

	@Test
	@DisplayName("정원을 초과하여 방에 입장할 수 없다")
	void test() {
		// given
		User host = createUser(1L);

		String password = "room password";
		Room room = createRoom(host, password, 1);
		room.enter(password);

		// when / then
		assertThatThrownBy(() -> room.enter(password))
			.isInstanceOf(RoomCapacityExceededException.class);
	}

	private Room createRoom(User host, String password, int capacity) {
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		host.createRoom(roomCode);

		return Room.builder()
			.code(roomCode)
			.title("황똥땡의 방")
			.createdAt(LocalDateTime.of(2024, 5, 3, 16, 30, 0))
			.capacity(capacity)
			.password(password)
			.build();
	}

	private User createUser(Long userId) {
		return User.builder()
			.id(userId)
			.nickname("황똥땡")
			.build();
	}

}
