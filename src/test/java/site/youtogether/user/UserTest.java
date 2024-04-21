package site.youtogether.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import site.youtogether.exception.user.HigherOrEqualRoleUserChangeException;

class UserTest {

	@Test
	@DisplayName("자신보다 낮은 역할의 유저를 자신과 같은 역할까지 올릴 수 있다")
	void changeRole() {
		// given
		String roomCode = "room code";

		User host = User.builder()
			.id(1L)
			.currentRoomCode(roomCode)
			.build();
		host.getHistory().put(roomCode, Role.HOST);
		User targetUser = User.builder()
			.id(2L)
			.currentRoomCode(roomCode)
			.build();
		targetUser.getHistory().put(roomCode, Role.VIEWER);

		// when
		host.changeOtherUserRole(roomCode, targetUser, Role.HOST);

		// then
		assertThat(targetUser.getRoleInCurrentRoom()).isEqualTo(Role.HOST);
	}

	@Test
	@DisplayName("자신과 동일하거나 높은 유저의 역할은 변경할 수 없다")
	void changeRoleFail() {
		// given
		String roomCode = "room code";

		User manager = User.builder()
			.id(1L)
			.currentRoomCode(roomCode)
			.build();
		manager.getHistory().put(roomCode, Role.MANAGER);
		User targetUser = User.builder()
			.id(2L)
			.currentRoomCode(roomCode)
			.build();
		targetUser.getHistory().put(roomCode, Role.MANAGER);

		// when / then
		assertThatThrownBy(() -> manager.changeOtherUserRole(roomCode, targetUser, Role.GUEST))
			.isInstanceOf(HigherOrEqualRoleUserChangeException.class);
	}

}
