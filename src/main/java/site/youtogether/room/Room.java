package site.youtogether.room;

import java.time.LocalDateTime;

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

	@Indexed
	private int participantCount;

	@Indexed
	private boolean activate;

	private int capacity;
	private String password;

	@Builder
	private Room(String code, String title, int capacity, String password, LocalDateTime createdAt, boolean activate) {
		this.code = code;
		this.title = title;
		this.capacity = capacity;
		this.createdAt = createdAt;
		this.password = password;
		this.activate = activate;
	}

	public boolean hasPassword() {
		return password != null;
	}

	public void changeTitle(User user, String updateTitle) {
		if (!user.isHost()) {
			throw new ChangeRoomTitleDeniedException();
		}
		title = updateTitle;
	}

	public void enter(String passwordInput) {
		if (password != null && !password.equals(passwordInput))
			throw new PasswordNotMatchException();

		if (participantCount >= capacity) {
			throw new RoomCapacityExceededException();
		}

		participantCount++;
		activate = true;
	}

	public void leave() {
		participantCount--;
	}

}
