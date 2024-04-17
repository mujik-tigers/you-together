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
		redisTemplate.opsForValue().set(USER_TRACKING_KEY_PREFIX + cookieValue, userId, TTL);

		return userId;
	}

	public Optional<Long> findByCookieValue(String cookieValue) {
		Long userId = redisTemplate.opsForValue().get(USER_TRACKING_KEY_PREFIX + cookieValue);

		return Optional.ofNullable(userId);
	}

	public void delete(String cookieValue) {
		redisTemplate.delete(USER_TRACKING_KEY_PREFIX + cookieValue);
	}

}
