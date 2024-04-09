package site.youtogether.user.infrastructure;

import static site.youtogether.util.AppConstants.*;

import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import site.youtogether.util.RandomUtil;

@Repository
@RequiredArgsConstructor
public class UserTrackingStorage {

	private final RedisTemplate<String, Long> redisTemplate;

	public boolean exists(String cookieValue) {
		Long userId = redisTemplate.opsForValue().get(USER_TRACKING_KEY_PREFIX + cookieValue);

		return userId != null;
	}

	public Long save(String cookieValue) {
		Long userId = RandomUtil.generateUserId();
		redisTemplate.opsForValue().set(USER_TRACKING_KEY_PREFIX + cookieValue, userId);

		return userId;
	}

	/**
	 * 클라이언트로부터 받은 쿠키값을 바탕으로 대응되는 유저 아이디를 찾는다.
	 *
	 * @param cookieValue 클라이언트로부터 받은 쿠키값
	 * @return userId
	 */
	public Optional<Long> findByCookieValue(String cookieValue) {
		Long userId = redisTemplate.opsForValue()
			.get(USER_TRACKING_KEY_PREFIX + cookieValue);

		return Optional.ofNullable(userId);
	}

}
