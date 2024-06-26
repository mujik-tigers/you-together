package site.youtogether.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.youtogether.user.Role;
import site.youtogether.user.User;

@AllArgsConstructor
@Getter
public class Participant {

	private final Long userId;

	private final String nickname;
	private final Role role;

	public Participant(User user) {
		this.userId = user.getId();
		this.nickname = user.getNickname();
		this.role = user.getRoleInCurrentRoom();
	}

}
