package site.youtogether.room;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import site.youtogether.exception.user.HigherOrEqualRoleChangeException;
import site.youtogether.exception.user.HigherOrEqualRoleUserChangeException;
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
		User changedUser = room.changeParticipantRole(HOST_ID, user.getUserId(), Role.VIEWER);

		// then
		assertThat(changedUser.getRole()).isEqualTo(Role.VIEWER);
	}

	@ParameterizedTest
	@DisplayName("역할을 변경하려는 유저의 등급은, 변경을 당하는 유저의 등급보다 높아야 한다")
	@EnumSource(Role.class)
	void changeUserMustHigherThanChangedUser(Role role) throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 14, 10, 37, 0));
		User user = createUser(3L, "연츠비", role);
		room.enterParticipant(user, null);

		// when // then
		assertThatThrownBy(() -> room.changeParticipantRole(user.getUserId(), HOST_ID, Role.VIEWER))
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
		assertThatThrownBy(() -> room.changeParticipantRole(HOST_ID, user.getUserId(), Role.HOST))
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

	private Room createRoom(LocalDateTime createTime) {
		User user = User.builder()
			.nickname("황똥땡")
			.userId(HOST_ID)
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
			.userId(userId)
			.nickname(nickname)
			.role(role)
			.build();
	}

}
