package site.youtogether.user.infrastructure;

import static org.assertj.core.api.Assertions.*;
import static site.youtogether.util.AppConstants.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import site.youtogether.IntegrationTestSupport;

class UserTrackingStorageTest extends IntegrationTestSupport {

	@Autowired
	private UserTrackingStorage userTrackingStorage;

	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	@AfterEach
	void clear() {
		redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
	}

	@Test
	@DisplayName("사용자 아이디 그룹에 새로운 아이디를 저장한다")
	void save() throws Exception {
		// given
		Long userId = 100L;

		// when
		userTrackingStorage.save(userId);

		// then
		Boolean isSaved = redisTemplate.opsForSet().isMember(USER_TRACKING_GROUP, userId);
		assertThat(isSaved).isTrue();
	}

	@Test
	@DisplayName("사용자 아이디가 그룹에 존재하는지 확인할 수 있다")
	void existsTrue() throws Exception {
		// given
		Long userId = 100L;
		redisTemplate.opsForSet().add(USER_TRACKING_GROUP, userId);

		// when
		boolean exists = userTrackingStorage.exists(userId);

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("사용자 아이디가 그룹에 없음을 확인할 수 있다")
	void existsFalse() throws Exception {
		// given
		Long userId = 100L;

		// when
		boolean exists = userTrackingStorage.exists(userId);

		// then
		assertThat(exists).isFalse();
	}

}
