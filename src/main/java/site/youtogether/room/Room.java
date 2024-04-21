package site.youtogether.room;

import java.time.LocalDateTime;
import java.util.Comparator;
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
import site.youtogether.exception.room.RoomEmptyException;
import site.youtogether.exception.user.ChangeRoomTitleDeniedException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.user.Role;
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
	private Map<Long, User> participants = new HashMap<>(10);

	@Builder
	private Room(String code, String title, int capacity, String password, LocalDateTime createdAt, User host) {
		this.code = code;
		this.title = title;
		this.capacity = capacity;
		this.createdAt = createdAt;
		this.password = password;

		participants.put(host.getId(), host);
	}

	public boolean hasPassword() {
		return password != null;
	}

	public void changeRoomTitle(Long userId, String updateTitle) {
		User user = findParticipantBy(userId);
		if (!user.isHost()) {
			throw new ChangeRoomTitleDeniedException();
		}
		title = updateTitle;
	}

	public void enterParticipant(User user, String passwordInput) {
		if (password != null) {
			if (!password.equals(passwordInput))
				throw new PasswordNotMatchException();
		}

		if (participants.size() >= capacity) {
			throw new RoomCapacityExceededException();
		}

		participants.put(user.getId(), user);
	}

	public void leaveParticipant(Long userId) {
		User user = findParticipantBy(userId);
		if (user.isHost()) {
			User delegatedUser = participants.values().stream()
				.filter(u -> !u.isHost())
				.sorted(Comparator.comparing(User::getPriority)
					.thenComparing(User::getId))
				.findFirst()
				.orElseThrow(RoomEmptyException::new);

			delegatedUser.changeRole(Role.HOST);
		}

		participants.remove(userId);
	}

	public User findParticipantBy(Long userId) {
		return Optional.ofNullable(participants.get(userId))
			.orElseThrow(UserNoExistenceException::new);
	}

	public void changeParticipantName(Long userId, String updateNickname) {
		User user = findParticipantBy(userId);
		user.changeNickname(updateNickname);
	}

	public User changeParticipantRole(Long userId, Long changedUserId, Role changeRole) {
		User user = findParticipantBy(userId);
		User changedUser = findParticipantBy(changedUserId);
		user.changeOtherUserRole(changedUser, changeRole);

		return changedUser;
	}

}
