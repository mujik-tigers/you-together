package site.youtogether.room;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import site.youtogether.util.AppConstants;
import site.youtogether.util.RandomUtil;

@RedisHash(value = "room", timeToLive = AppConstants.day)
@Getter
public class Room {

	@Id
	private final String code;

	private final String title;
	private final int capacity;
	private final String password;

	@Builder
	public Room(String title, int capacity, String password) {
		this.code = RandomUtil.generateRoomCode();
		this.title = title;
		this.capacity = capacity;
		this.password = password;
	}

}
