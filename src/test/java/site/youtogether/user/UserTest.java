package site.youtogether.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import site.youtogether.exception.user.HigherOrEqualRoleUserChangeException;
import site.youtogether.exception.user.UserNotEnteringException;
import site.youtogether.exception.user.UsersInDifferentRoomException;

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
		host.changeOtherUserRole(targetUser, Role.HOST);

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
		assertThatThrownBy(() -> manager.changeOtherUserRole(targetUser, Role.GUEST))
			.isInstanceOf(HigherOrEqualRoleUserChangeException.class);
	}

	@Test
	@DisplayName("현재 참여중인 방이 없으면 다른 유저의 역할을 변경할 수 없다")
	void noParticipantChangeRoleFail() throws Exception {
		// given
		String roomCode = "room code";

		User user = User.builder()
			.id(1L)
			.build();
		user.getHistory().put(roomCode, Role.HOST);

		User targetUser = User.builder()
			.id(2L)
			.currentRoomCode(roomCode)
			.build();
		targetUser.getHistory().put(roomCode, Role.GUEST);

		// when / then
		assertThatThrownBy(() -> user.changeOtherUserRole(targetUser, Role.MANAGER))
			.isInstanceOf(UserNotEnteringException.class);
	}

	@Test
	@DisplayName("역할을 바꾸려는 유저가 현재 참여중인 방이 없으면 역할을 변경할 수 없다")
	void targetUserNoParticipantChangeRoleFail() throws Exception {
		// given
		String roomCode = "room code";

		User user = User.builder()
			.id(1L)
			.currentRoomCode(roomCode)
			.build();
		user.getHistory().put(roomCode, Role.HOST);

		User targetUser = User.builder()
			.id(2L)
			.build();
		targetUser.getHistory().put(roomCode, Role.GUEST);

		// when / then
		assertThatThrownBy(() -> user.changeOtherUserRole(targetUser, Role.MANAGER))
			.isInstanceOf(UserNotEnteringException.class);
	}

	@Test
	@DisplayName("역할을 바꾸려는 유저와 같은 방이 아니라면 역할을 변경할 수 없다")
	void noSameRoomChangeRoleFail() throws Exception {
		// given
		String roomCode1 = "room code1";
		String roomCode2 = "room code2";

		User user = User.builder()
			.id(1L)
			.currentRoomCode(roomCode1)
			.build();
		user.getHistory().put(roomCode1, Role.HOST);

		User targetUser = User.builder()
			.id(2L)
			.currentRoomCode(roomCode2)
			.build();
		targetUser.getHistory().put(roomCode2, Role.GUEST);

		// when / then
		assertThatThrownBy(() -> user.changeOtherUserRole(targetUser, Role.MANAGER))
			.isInstanceOf(UsersInDifferentRoomException.class);
	}

	@Test
	@DisplayName("닉네임을 변경할 수 있다")
	void changeNickname() throws Exception {
		// given
		String roomCode = "room code";

		User user = User.builder()
			.id(1L)
			.currentRoomCode(roomCode)
			.nickname("황똥땡")
			.build();
		user.getHistory().put(roomCode, Role.HOST);

		String updateNickname = "황츠비";

		// when
		user.changeNickname(updateNickname);

		// then
		assertThat(user.getNickname()).isEqualTo(updateNickname);
	}

	@Test
	@DisplayName("방 참여 여부와 상관없이 닉네임을 변경할 수 있다")
	void changeNicknameNoEnterRoom() throws Exception {
		// given
		User user = User.builder()
			.id(1L)
			.nickname("황똥땡")
			.build();

		String updateNickname = "황츠비";

		// when
		user.changeNickname(updateNickname);

		// then
		assertThat(user.getNickname()).isEqualTo(updateNickname);
	}

	@Test
	@DisplayName("방 참여 기록은 최대 10개까지 저장된다")
	void historyLength() throws Exception {
		// given
		User user = User.builder()
			.id(1L)
			.nickname("황똥땡")
			.build();

		// when
		for (int i = 0; i < 10; i++) {
			user.enterRoom("roomCode" + i);
		}

		// then
		assertThat(user.getHistory().size()).isEqualTo(10);
	}

	@Test
	@DisplayName("방 참여 기록이 10개를 넘어가면, 가장 오래전에 방문한 방의 기록부터 제거한다")
	void historyLimitExceed() throws Exception {
		// given
		User user = User.builder()
			.id(1L)
			.nickname("황똥땡")
			.build();

		String firstRoomCode = "firstRoomCode";
		user.enterRoom(firstRoomCode);

		// when
		for (int i = 0; i < 10; i++) {
			user.enterRoom("roomCode" + i);
		}

		// then
		assertThat(user.getHistory().size()).isEqualTo(10);
		assertThat(user.getHistory()).doesNotContainKey(firstRoomCode);
	}

	@Test
	@DisplayName("방을 재입장 한 경우, 방문 순서가 갱신된다")
	void historyUpdate() throws Exception {
		// given
		User user = User.builder()
			.id(1L)
			.nickname("황똥땡")
			.build();

		String firstRoomCode = "firstRoomCode";
		user.enterRoom(firstRoomCode);

		// when
		for (int i = 0; i < 3; i++) {
			user.enterRoom("roomCode" + i);
		}
		user.enterRoom(firstRoomCode);

		// then
		assertThat(user.getHistory().size()).isEqualTo(4);
		assertThat(user.getRoomCodeQueue()).containsExactly("roomCode0", "roomCode1", "roomCode2", "firstRoomCode");
	}

}
