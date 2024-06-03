package site.youtogether.util;

import static site.youtogether.util.AppConstants.*;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.youtogether.playlist.infrastructure.PlayingVideoStorage;

@Component
@RequiredArgsConstructor
public class DataCleaner {

	private final DefaultRedisScript<List> batchRemoveScript;
	private final RedisTemplate<String, String> redisTemplate;
	private final PlayingVideoStorage playingVideoStorage;

	@Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
	public void clean() {
		List erasedRoomKey = redisTemplate.execute(batchRemoveScript,
			List.of("site.youtogether.room.RoomIdx", "site.youtogether.user.UserIdx", USER_NICKNAME_SET));

		for (int i = 1; i < erasedRoomKey.size(); i++) {
			String erasedRoomCode = erasedRoomKey.get(i).toString().substring("room:".length());
			playingVideoStorage.delete(erasedRoomCode);
		}
	}

}
