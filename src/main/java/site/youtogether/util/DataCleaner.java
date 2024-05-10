package site.youtogether.util;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataCleaner {

	private final DefaultRedisScript<Void> batchRemoveScript;
	private final RedisTemplate<String, String> redisTemplate;

	@Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
	public void clean() {
		redisTemplate.execute(batchRemoveScript,
			List.of("site.youtogether.room.RoomIdx", "site.youtogether.user.UserIdx", "site.youtogether.playlist.PlaylistIdx"));
	}

}
