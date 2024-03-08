package site.youtogether.room;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Builder;
import lombok.Getter;
import site.youtogether.user.User;
import site.youtogether.util.RandomUtils;
import site.youtogether.video.Video;

@Getter
public class Room {

	private final String code;
	private final String title;
	private int capacity;
	private String password;
	private final Map<String, User> participants = new ConcurrentHashMap<>();
	private final Map<String, Video> playlist = new LinkedHashMap<>();

	@Builder
	public Room(String title, int capacity, String password) {
		this.code = RandomUtils.generateRoomCode();
		this.title = title;
		this.capacity = capacity;
		this.password = password;
	}

	public void addParticipant(String address, User user) {
		participants.put(address, user);
	}

}
