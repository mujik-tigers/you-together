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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(value = "room")
public class Room {

	@Id
	private String code;

	@Searchable
	private String title;

	private int capacity;
	private String password;
	private User host;
	private Map<String, User> participants = new HashMap<>(10);

	@Indexed
	private LocalDateTime createAt;

	@Builder
	public Room(String title, int capacity, String password, User host, LocalDateTime createAt) {
		this.code = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		this.capacity = capacity;
		this.title = title;
		this.password = password;
		this.host = host;
		this.createAt = createAt;

		participants.put(host.getSessionCode(), host);
	}

}
