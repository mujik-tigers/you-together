package site.youtogether.user;

import lombok.Builder;

public record User(String address, String nickname, Role role) {

	@Builder
	public User {
	}

}
