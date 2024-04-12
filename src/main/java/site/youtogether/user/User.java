package site.youtogether.user;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

	@Id
	private final Long userId;

	private String nickname;
	private Role role;

	@Builder
	private User(Long userId, String nickname, Role role) {
		this.userId = userId;
		this.nickname = nickname;
		this.role = role;
	}

	public void changeNickname(String updateNickname) {
		this.nickname = updateNickname;
	}

}
