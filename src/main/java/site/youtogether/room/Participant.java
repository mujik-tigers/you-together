package site.youtogether.room;

import lombok.Getter;
import site.youtogether.user.Role;
import site.youtogether.user.User;

@Getter
public class Participant {

	private final Long id;

	private final String nickname;
	private final Role role;

	public Participant(User user) {
		this.id = user.getId();
		this.nickname = user.getNickname();
		this.role = user.getRoleInCurrentRoom();
	}

}
