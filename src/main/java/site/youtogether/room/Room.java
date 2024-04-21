package site.youtogether.room;

import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.exception.room.PasswordNotMatchException;
import site.youtogether.exception.room.RoomCapacityExceededException;
import site.youtogether.exception.room.UserAbsentException;
import site.youtogether.exception.user.ChangeRoomTitleDeniedException;
import site.youtogether.user.User;

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
	private Map<Long, Participant> participants = new HashMap<>(10);

	@TimeToLive
	private final Long expirationTime = TIME_TO_LIVE;

	@Builder
	private Room(String code, String title, int capacity, String password, LocalDateTime createdAt, User host) {
		this.code = code;
		this.title = title;
		this.capacity = capacity;
		this.createdAt = createdAt;
		this.password = password;

		participants.put(host.getId(), new Participant(host));
	}

	public boolean hasPassword() {
		return password != null;
	}

	public void changeRoomTitle(User user, String updateTitle) {
		validateParticipantExist(user.getId());
		if (!user.isHost()) {
			throw new ChangeRoomTitleDeniedException();
		}
		title = updateTitle;
	}

	public void enterParticipant(User user, String passwordInput) {
		if (password != null && !password.equals(passwordInput))
			throw new PasswordNotMatchException();

		if (participants.size() >= capacity) {
			throw new RoomCapacityExceededException();
		}

		participants.put(user.getId(), new Participant(user));
	}

	public void leaveParticipant(Long userId) {
		validateParticipantExist(userId);
		participants.remove(userId);
	}

	public void updateParticipant(User user) {
		validateParticipantExist(user.getId());
		participants.put(user.getId(), new Participant(user));
	}

	private void validateParticipantExist(Long userId) {
		if (!participants.containsKey(userId)) {
			throw new UserAbsentException();
		}
	}

}
