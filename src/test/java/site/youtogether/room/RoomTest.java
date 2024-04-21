package site.youtogether.room;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import site.youtogether.exception.user.ChangeRoomTitleDeniedException;
import site.youtogether.exception.user.HigherOrEqualRoleChangeException;
import site.youtogether.exception.user.HigherOrEqualRoleUserChangeException;
import site.youtogether.exception.user.NotManageableUserException;
import site.youtogether.exception.user.SelfRoleChangeException;
import site.youtogether.user.Role;
import site.youtogether.user.User;

class RoomTest {

	private static final Long HOST_ID = 100L;

	@Test
	@DisplayName("특정 유저의 역할을 변경할 수 있다")
	void changeParticipantRole() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0));
		User user = createUser(3L, "연츠비", Role.GUEST);
		room.enterParticipant(user, null);

		// when
		User changedUser = room.changeParticipantRole(HOST_ID, user.getId(), Role.VIEWER);

		// then
		assertThat(changedUser.getRole()).isEqualTo(Role.VIEWER);
	}

	@Test
	@DisplayName("매니저보다 낮은 역할을 가진 유저는 역할을 변경할 수 없다")
	void notManageableUserChangeFail() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0));
		User user1 = createUser(3L, "연츠비", Role.EDITOR);
		User user2 = createUser(4L, "연츠비", Role.GUEST);

		room.enterParticipant(user1, null);
		room.enterParticipant(user2, null);

		// when
		assertThatThrownBy(() -> room.changeParticipantRole(user1.getId(), user2.getId(), Role.VIEWER))
			.isInstanceOf(NotManageableUserException.class);
	}

	@ParameterizedTest
	@DisplayName("역할을 변경하려는 유저의 등급은, 변경을 당하는 유저의 등급보다 높아야 한다")
	@EnumSource(value = Role.class, names = {"HOST", "MANAGER"})
	void changeUserMustHigherThanChangedUser(Role role) throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0));
		User user = createUser(3L, "연츠비", role);
		room.enterParticipant(user, null);

		// when // then
		assertThatThrownBy(() -> room.changeParticipantRole(user.getId(), HOST_ID, Role.VIEWER))
			.isInstanceOf(HigherOrEqualRoleUserChangeException.class);
	}

	@Test
	@DisplayName("역할을 변경하려는 유저의 등급과 같거나 높은 등급으로의 변경은 불가능하다")
	void changeRoleMustLowerThanChangeUser() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0));
		User user = createUser(3L, "연츠비", Role.GUEST);
		room.enterParticipant(user, null);

		// when // then
		assertThatThrownBy(() -> room.changeParticipantRole(HOST_ID, user.getId(), Role.HOST))
			.isInstanceOf(HigherOrEqualRoleChangeException.class);
	}

	@Test
	@DisplayName("자기 자신의 역할을 변경할 수 없다")
	void selfChangeRoleFail() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0));

		// when // then
		assertThatThrownBy(() -> room.changeParticipantRole(HOST_ID, HOST_ID, Role.GUEST))
			.isInstanceOf(SelfRoleChangeException.class);
	}

	@Test
	@DisplayName("호스트는 방 제목을 변경할 수 있다")
	void changeRoomTitle() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0));
		String originTitle = room.getTitle();
		String updateTitle = "연츠비의 방";

		// when
		room.changeRoomTitle(HOST_ID, updateTitle);

		// then
		assertThat(room.getTitle()).isNotEqualTo(originTitle);
		assertThat(room.getTitle()).isEqualTo(updateTitle);
	}

	@Test
	@DisplayName("호스트가 아닌 유저는 방 제목을 변경할 수 없다")
	void changeRoomTitleFail() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0));
		User user = createUser(2L, "연츠비", Role.MANAGER);
		room.enterParticipant(user, null);

		String updateTitle = "연츠비의 방";

		// when // then
		assertThatThrownBy(() -> room.changeRoomTitle(user.getId(), updateTitle))
			.isInstanceOf(ChangeRoomTitleDeniedException.class);
	}

	@Test
	@DisplayName("호스트가 방을 떠나는 경우, 차등급의 유저 중, 가장 먼저 들어온 유저에게 호스트를 넘긴다")
	void hostLeaveRoom() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0));
		User user1 = createUser(2L, "황츠비", Role.GUEST);
		User user2 = createUser(3L, "연츠비", Role.MANAGER);
		User user3 = createUser(4L, "연똥땡", Role.MANAGER);

		room.enterParticipant(user1, null);
		room.enterParticipant(user2, null);
		room.enterParticipant(user3, null);

		// when
		room.leaveParticipant(HOST_ID);

		// then
		Map<Long, User> participants = room.getParticipants();

		assertThat(participants).doesNotContainKey(HOST_ID);
		assertThat(user2.getRole()).isEqualTo(Role.HOST);
	}

	private Room createRoom(LocalDateTime createTime) {
		User user = User.builder()
			.nickname("황똥땡")
			.id(HOST_ID)
			.role(Role.HOST)
			.build();

		return Room.builder()
			.title("황똥땡의 방")
			.host(user)
			.createdAt(createTime)
			.capacity(10)
			.build();
	}

	private User createUser(Long userId, String nickname, Role role) {
		return User.builder()
			.id(userId)
			.nickname(nickname)
			.role(role)
			.build();
	}

}
