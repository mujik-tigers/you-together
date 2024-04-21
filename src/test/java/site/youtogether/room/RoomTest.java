package site.youtogether.room;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import site.youtogether.exception.room.UserAbsentException;
import site.youtogether.exception.user.ChangeRoomTitleDeniedException;
import site.youtogether.user.User;
import site.youtogether.util.RandomUtil;

class RoomTest {

	@Test
	@DisplayName("호스트는 방 제목을 변경할 수 있다")
	void changeRoomTitle() throws Exception {
		// given
		User host = createUser(1L);
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0), host);
		String originTitle = room.getTitle();
		String updateTitle = "연츠비의 방";

		// when
		room.changeRoomTitle(host, updateTitle);

		// then
		assertThat(room.getTitle()).isNotEqualTo(originTitle);
		assertThat(room.getTitle()).isEqualTo(updateTitle);
	}

	@Test
	@DisplayName("현재 방에서 호스트여도, 다른 방의 제목을 변경할 순 없다")
	void otherRoomTitleChangeFail() throws Exception {
		// given
		User host = createUser(1L);
		Room room1 = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0), host);

		Room room2 = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0), createUser(2L));
		String updateTitle = "연츠비의 방";

		// when // then
		assertThatThrownBy(() -> room2.changeRoomTitle(host, updateTitle))
			.isInstanceOf(UserAbsentException.class);
	}

	@Test
	@DisplayName("호스트가 아닌 유저는 방 제목을 변경할 수 없다")
	void changeRoomTitleFail() throws Exception {
		// given
		User host = createUser(1L);
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0), host);

		User user = createUser(2L);
		user.enterRoom(room.getCode());
		room.enterParticipant(user, null);

		String updateTitle = "연츠비의 방";

		// when // then
		assertThatThrownBy(() -> room.changeRoomTitle(user, updateTitle))
			.isInstanceOf(ChangeRoomTitleDeniedException.class);
	}

	private Room createRoom(LocalDateTime createTime, User host) {
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		host.createRoom(roomCode);

		return Room.builder()
			.code(roomCode)
			.title("황똥땡의 방")
			.host(host)
			.createdAt(createTime)
			.capacity(10)
			.build();
	}

	private User createUser(Long userId) {
		return User.builder()
			.nickname("황똥땡")
			.id(userId)
			.build();
	}

}
