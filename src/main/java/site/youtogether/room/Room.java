package site.youtogether.room;

import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.exception.room.PasswordNotMatchException;
import site.youtogether.exception.room.RoomCapacityExceededException;
import site.youtogether.exception.user.UserNoExistenceException;
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
	private Map<Long, User> participants = new HashMap<>(10);

	@Builder
	private Room(String title, int capacity, String password, LocalDateTime createdAt, User host) {
		this.code = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);
		this.title = title;
		this.capacity = capacity;
		this.createdAt = createdAt;
		this.password = password;
		this.host = host;

		participants.put(host.getUserId(), host);
	}

	public User findParticipantBy(Long userId) {
		return Optional.ofNullable(participants.get(userId))
			.orElseThrow(UserNoExistenceException::new);
	}

	public boolean hasPassword() {
		return password != null;
	}

	public void enterParticipant(User user, String passwordInput) {
		if (password != null) {
			if (!password.equals(passwordInput))
				throw new PasswordNotMatchException();
		}

		if (participants.size() >= capacity) {
			throw new RoomCapacityExceededException();
		}

		participants.put(user.getUserId(), user);
	}

	public void leaveParticipant(Long userId) {
		participants.remove(userId);
	}

	public User changeParticipantName(Long userId, String updateNickname) {
		User user = findParticipantBy(userId);
		user.changeNickname(updateNickname);

		if (userId.equals(host.getUserId())) {
			host = user;
		}

		return user;
	}

}
