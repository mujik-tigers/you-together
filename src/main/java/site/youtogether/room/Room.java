package site.youtogether.room;

import static site.youtogether.util.AppConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.user.User;
import site.youtogether.util.RandomUtil;

@RedisHash(value = "room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Room {

	@Id
	private String code;

	private int capacity;
	private String title;
	private String password;
	private User host;
	private Map<String, User> participants = new HashMap<>(MAXIMUM_ROOM_CAPACITY);

	@Builder
	public Room(String title, int capacity, String password, User host) {
		this.code = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		this.capacity = capacity;
		this.title = title;
		this.password = password;
		this.host = host;

		participants.put(host.getSessionCode(), host);
	}

	public void enter(User user) {
		participants.put(user.getSessionCode(), user);
	}

	public User leave(String sessionCode) {
		return participants.remove(sessionCode);
	}

}
