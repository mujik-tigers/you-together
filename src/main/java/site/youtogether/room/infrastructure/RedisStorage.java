package site.youtogether.room.infrastructure;

import static site.youtogether.util.AppConstants.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisStorage {

	private final RedisTemplate<String, String> redisStringTemplate;

	public Boolean existsInActiveAddress(String address) {
		return redisStringTemplate.opsForSet().isMember(ACTIVE_ADDRESS_KEY, address);
	}

	public void addActiveAddress(String address) {
		redisStringTemplate.opsForSet().add(ACTIVE_ADDRESS_KEY, address);
	}

	public void addParticipant(String roomCode, String address) {
		String participantKey = PARTICIPANTS_KEY_PREFIX + roomCode;
		redisStringTemplate.opsForSet().add(participantKey, address);
	}

}
