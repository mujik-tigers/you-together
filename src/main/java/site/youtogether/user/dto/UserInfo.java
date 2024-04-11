package site.youtogether.user.dto;

import lombok.Getter;
import site.youtogether.user.Role;
import site.youtogether.user.User;

@Getter
public class UserInfo {

	private final Long userId;
	private final String nickname;
	private final Role role;

	public UserInfo(User user) {
		this.userId = user.getUserId();
		this.nickname = user.getNickname();
		this.role = user.getRole();
	}

}
