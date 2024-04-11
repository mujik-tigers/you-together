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
	private final RedisTemplate<String, String> stringRedisTemplate;

	public boolean exists(String cookieValue) {
		Long userId = redisTemplate.opsForValue().get(USER_TRACKING_KEY_PREFIX + cookieValue);

		return userId != null;
	}

	public Long save(String cookieValue) {
		Long userId = RandomUtil.generateUserId();
		redisTemplate.opsForValue().set(USER_TRACKING_KEY_PREFIX + cookieValue, userId);
		stringRedisTemplate.opsForValue().set(USER_ID_KEY_PREFIX + userId, cookieValue);

		return userId;
	}

	public Optional<Long> findByCookieValue(String cookieValue) {
		Long userId = redisTemplate.opsForValue().get(USER_TRACKING_KEY_PREFIX + cookieValue);

		return Optional.ofNullable(userId);
	}

	public void delete(Long userId) {
		String cookieValue = stringRedisTemplate.opsForValue().get(USER_ID_KEY_PREFIX + userId);

		redisTemplate.delete(USER_TRACKING_KEY_PREFIX + cookieValue);
		redisTemplate.delete(USER_ID_KEY_PREFIX + userId);
	}

}
