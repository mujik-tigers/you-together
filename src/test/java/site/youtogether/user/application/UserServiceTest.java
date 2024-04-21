package site.youtogether.user.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.user.infrastructure.UserStorage;

class UserServiceTest extends IntegrationTestSupport {

	private static final Long HOST_ID = 100L;

	@Autowired
	private UserService userService;

	@Autowired
	private UserStorage userStorage;

	@Autowired
	private RoomStorage roomStorage;

	@AfterEach
	void clean() {
		userStorage.deleteAll();
		roomStorage.deleteAll();
	}

	@Test
	@DisplayName("닉네임을 변경한다")
	void updateNickname() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 11, 4, 8, 0), "황똥땡의 방", "호스트 황똥땡");

		User user = createUser("연츠비", room.getCode(), Role.GUEST);
		room.enterParticipant(user, null);
		roomStorage.save(room);

		String updateNickname = "연똥땡";

		// when
		Participant participantInfo = userService.changeUserNickname(user.getId(), updateNickname, room.getCode());

		// then
		assertThat(participantInfo.getNickname()).isEqualTo(updateNickname);

		Room savedRoom = roomStorage.findById(room.getCode()).get();
		User participant = savedRoom.getParticipants().get(user.getId());
		assertThat(participant.getNickname()).isEqualTo(updateNickname);

		User savedUser = userStorage.findById(user.getId()).get();
		assertThat(savedUser.getNickname()).isEqualTo(updateNickname);
	}

	@Test
	@DisplayName("특정 유저의 역할을 변경한다")
	void changeUserRole() throws Exception {
		// given
		Room room = createRoom(LocalDateTime.of(2024, 4, 11, 4, 8, 0), "황똥땡의 방", "황똥땡");

		User user = createUser("연츠비", room.getCode(), Role.GUEST);
		room.enterParticipant(user, null);
		roomStorage.save(room);

		UserRoleChangeForm userRoleChangeForm = new UserRoleChangeForm(room.getCode(), user.getId(), Role.VIEWER);

		// when
		userService.changeUserRole(HOST_ID, userRoleChangeForm);

		// then
		Room savedRoom = roomStorage.findById(room.getCode()).get();
		User changedUser = userStorage.findById(user.getId()).get();

		assertThat(changedUser.getId()).isEqualTo(user.getId());
		assertThat(changedUser.getRole()).isEqualTo(Role.VIEWER);
	}

	private Room createRoom(LocalDateTime createTime, String title, String hostNickname) {
		User user = User.builder()
			.nickname(hostNickname)
			.id(HOST_ID)
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

	private User createUser(String nickname, String currentRoomCode, Role role) {
		User user = User.builder()
			.id(3L)
			.nickname(nickname)
			.role(role)
			.currentRoomCode(currentRoomCode)
			.build();
		userStorage.save(user);
		return user;
	}

}
