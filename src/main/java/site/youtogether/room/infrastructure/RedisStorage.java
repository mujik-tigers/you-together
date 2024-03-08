package site.youtogether.room.infrastructure;

import static site.youtogether.util.AppConstants.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisStorage {

	private final RedisTemplate<String, String> redisStringTemplate;

	public Boolean existsInHostingList(String address) {
		return redisStringTemplate.opsForSet().isMember(HOSTING_KEY, address);
	}

	public Boolean existsInWatchingList(String address) {
		return redisStringTemplate.opsForSet().isMember(WATCHING_KEY, address);
	}

	public void addHostingAddress(String address) {
		redisStringTemplate.opsForSet().add(HOSTING_KEY, address);
	}

	public void addParticipant(String roomCode, String address) {
		String participantKey = PARTICIPANTS_KEY_PREFIX + roomCode;
		redisStringTemplate.opsForSet().add(participantKey, address);
	}

}
