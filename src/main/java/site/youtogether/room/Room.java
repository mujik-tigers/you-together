package site.youtogether.room;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import site.youtogether.user.User;
import site.youtogether.util.RandomUtil;

@RedisHash(value = "room")
@Getter
public class Room {

	@Id
	private final String code;

	private final int capacity;
	private final String title;
	private final String password;
	private final String host;
	private final Map<String, User> participants = new HashMap<>(10);

	@Builder
	public Room(String title, int capacity, String password, User host) {
		this.code = RandomUtil.generateRoomCode();
		this.capacity = capacity;
		this.title = title;
		this.password = password;
		this.host = host.getSessionCode();

		participants.put(host.getSessionCode(), host);
	}

}
