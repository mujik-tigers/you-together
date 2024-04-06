package site.youtogether.room;

import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.user.User;
import site.youtogether.util.RandomUtil;

@Document(value = "room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Room {

	@Id
	private String code;

	@Searchable
	private String title;

	@Indexed
	private LocalDateTime createdAt;

	private int capacity;
	private String password;
	private User host;
	private Map<String, User> participants = new HashMap<>(10);

	@Builder
	public Room(String title, int capacity, String password, LocalDateTime createdAt, User host) {
		this.code = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		this.title = title;
		this.capacity = capacity;
		this.createdAt = createdAt;
		this.password = password;
		this.host = host;

		participants.put(host.getSessionCode(), host);
	}

	public boolean hasPassword() {
		return password != null;
	}

}
