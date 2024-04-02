package site.youtogether.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.youtogether.user.User;

@AllArgsConstructor
@Getter
public class UserNickname {

	private final String nickname;

	public UserNickname(User user) {
		this.nickname = user.getNickname();
	}

}
