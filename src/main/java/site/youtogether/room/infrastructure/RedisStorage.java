package site.youtogether.room.infrastructure;

import static site.youtogether.util.AppConstants.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisStorage {

	private final RedisTemplate<String, String> redisStringTemplate;

	public Boolean isParticipant(String sessionCode) {
		return redisStringTemplate.opsForSet().isMember(PARTICIPANTS_KEY, sessionCode);
	}

	public void addParticipant(String sessionCode) {
		redisStringTemplate.opsForSet().add(PARTICIPANTS_KEY, sessionCode);
	}

}
