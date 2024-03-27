package site.youtogether.user.dto;

import lombok.Getter;
import site.youtogether.user.User;

@Getter
public class UserNickname {

	private final String nickname;

	public UserNickname(User user) {
		this.nickname = user.getNickname();
	}

}
