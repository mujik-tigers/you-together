package site.youtogether.user.infrastructure;

import static site.youtogether.util.AppConstants.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserTrackingStorage {

	private final RedisTemplate<String, Long> redisTemplate;

	public boolean exists(Long userId) {
		return redisTemplate.opsForSet().isMember(USER_TRACKING_GROUP, userId);
	}

	public void save(Long userId) {
		redisTemplate.opsForSet().add(USER_TRACKING_GROUP, userId);
	}

	public void delete(Long userId) {
		redisTemplate.opsForSet().remove(USER_TRACKING_GROUP, userId);
	}

}
