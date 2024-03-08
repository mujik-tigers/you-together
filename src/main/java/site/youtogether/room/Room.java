package site.youtogether.room;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.participant.User;

@RedisHash("room")
@NoArgsConstructor
@Getter
public class Room {

	private static final int UUID_LENGTH = 8;

	@Id
	private String id;

	private String name;
	private String password;
	private int totalCapacity;
	private Map<String, User> participants = new HashMap<>();

	@Builder
	private Room(String name, String password, int totalCapacity, User creator) {
		this.id = generateRoomId();
		this.name = name;
		this.password = password;
		this.totalCapacity = totalCapacity;
		this.participants.put(creator.getUserIp(), creator);
	}

	private String generateRoomId() {
		return UUID.randomUUID().toString().substring(0, UUID_LENGTH);
	}

}
