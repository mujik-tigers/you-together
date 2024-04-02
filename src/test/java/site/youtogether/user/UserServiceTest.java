package site.youtogether.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.youtogether.IntegrationTestSupport;
import site.youtogether.user.dto.UserNickname;
import site.youtogether.user.infrastructure.UserStorage;

class UserServiceTest extends IntegrationTestSupport {

	@Autowired
	private UserStorage userStorage;

	@Autowired
	private UserService userService;

	@Test
	@DisplayName("세션 유저의 닉네임을 가져올 수 있다")
	void fetchUserNickname() throws Exception {
		// given
		String sessionCode = "adlkfjalsd";
		User user = User.builder()
			.sessionCode(sessionCode)
			.nickname("개구장이")
			.address("127.0.0.1")
			.role(Role.GUEST)
			.build();
		userStorage.save(user);

		// when
		UserNickname userNickname = userService.fetchUserNickname(sessionCode);

		// then
		assertThat(userNickname.getNickname()).isEqualTo(user.getNickname());
	}

	@Test
	@DisplayName("세션 유저의 닉네임을 변경할 수 있다")
	void updateUserNickname() throws Exception {
		// given
		String sessionCode = "dalfhalsdk";
		User user = User.builder()
			.sessionCode(sessionCode)
			.nickname("개구장이")
			.address("127.0.0.1")
			.role(Role.GUEST)
			.build();
		userStorage.save(user);
		String updateName = "안개구장이";

		// when
		UserNickname userNickname = userService.updateUserNickname(sessionCode, updateName);

		// then
		assertThat(userNickname.getNickname()).isEqualTo(updateName);
	}

}
