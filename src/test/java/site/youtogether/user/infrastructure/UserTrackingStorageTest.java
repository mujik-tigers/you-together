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
	@DisplayName("랜덤 유저 아이디를 만들고, 해당 유저 아이디를 클라이언트로부터 받은 쿠키 값에 대응되도록 저장한다")
	void save() throws Exception {
		// given
		String cookieValue = "dlkafldjkfldlkfajlds";

		// when
		Long userId = userTrackingStorage.save(cookieValue);

		// then
		Long saved = redisTemplate.opsForValue().get(USER_TRACKING_KEY_PREFIX + cookieValue);
		assertThat(saved).isEqualTo(userId);
	}

	@Test
	@DisplayName("클라이언트로부터 받은 쿠키 값을 사용하여 대응되는 유저 아이디를 찾는다")
	void findByCookieValue() throws Exception {
		// given
		String cookieValue = "asdklhlkasdghklashg";
		Long userId = 1L;
		redisTemplate.opsForValue()
			.set(USER_TRACKING_KEY_PREFIX + cookieValue, userId);

		// when
		Long findUserId = userTrackingStorage.findByCookieValue(cookieValue).get();

		// then
		assertThat(findUserId).isEqualTo(userId);
	}

	@Test
	@DisplayName("클라이언트로부터 받은 쿠키 값과 대응되는 유저 아이디가 없는 경우 예외가 발생한다")
	void findByCookieFail() throws Exception {
		// given
		String cookieValue = "asdklhlkasdghklashg";

		// when // then
		assertThat(userTrackingStorage.findByCookieValue(cookieValue)).isEmpty();
	}

}
