package site.youtogether.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;

@RedisHash(value = "user")
@Getter
public class User {

	@Id
	private final String sessionCode;

	private final String address;
	private String nickname;
	private final Role role;

	@Builder
	public User(String sessionCode, String address, String nickname, Role role) {
		this.sessionCode = sessionCode;
		this.address = address;
		this.nickname = nickname;
		this.role = role;
	}

	public void changeNickname(String changeName) {
		this.nickname = changeName;
	}

}
