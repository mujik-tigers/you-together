package site.youtogether.user.infrastructure;

import static site.youtogether.util.AppConstants.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.cookie.CookieInvalidException;
import site.youtogether.util.RandomUtil;

@Repository
@RequiredArgsConstructor
public class UserTrackingStorage {

	private final RedisTemplate<String, String> redisTemplate;

	public boolean exists(String sessionCode) {
		String userCode = redisTemplate.opsForValue().get(USER_TRACKING_KEY_PREFIX + sessionCode);

		return userCode != null;
	}

	public void save(String sessionCode) {
		redisTemplate.opsForValue().set(USER_TRACKING_KEY_PREFIX + sessionCode, RandomUtil.generateSessionCode().toString());
	}

	/**
	 * 클라이언트로부터 받은 쿠키값을 바탕으로 대응되는 유저 아이디를 찾는다.
	 *
	 * @param cookieValue 클라이언트로부터 받은 쿠키값
	 * @return userId
	 */
	public String findByCookieValue(String cookieValue) {
		String userId = redisTemplate.opsForValue()
			.get(USER_TRACKING_KEY_PREFIX + cookieValue);

		if (userId == null) {
			throw new CookieInvalidException();
		}

		return userId;
	}

}
