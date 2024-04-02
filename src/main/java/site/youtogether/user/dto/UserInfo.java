package site.youtogether.user.dto;

import lombok.Getter;
import site.youtogether.user.User;

@Getter
public class UserInfo {

	private final String nickname;

	public UserInfo(User user) {
		this.nickname = user.getNickname();
	}

}
