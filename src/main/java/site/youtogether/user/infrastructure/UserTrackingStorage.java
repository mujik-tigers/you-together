package site.youtogether.user.infrastructure;

import static site.youtogether.util.AppConstants.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import site.youtogether.util.RandomUtil;

@Repository
@RequiredArgsConstructor
public class UserTrackingStorage {

	private final RedisTemplate<String, String> redisStringTemplate;

	public boolean exists(String sessionCode) {
		String userCode = redisStringTemplate.opsForValue().get(SESSION_CODE_KEY_PREFIX + sessionCode);

		return userCode != null;
	}

	public void save(String sessionCode) {
		redisStringTemplate.opsForValue().set(SESSION_CODE_KEY_PREFIX + sessionCode, RandomUtil.generateSessionCode().toString());
	}

	public void deleteBy(String sessionCode) {
		redisStringTemplate.delete(SESSION_CODE_KEY_PREFIX + sessionCode);
	}

}
