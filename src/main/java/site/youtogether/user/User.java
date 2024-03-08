package site.youtogether.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import site.youtogether.util.AppConstants;

@RedisHash(value = "user", timeToLive = AppConstants.day)
@Getter
public class User {

	@Id
	private final String address;

	private final String nickname;
	private final Role role;

	@Builder
	public User(String address, String nickname, Role role) {
		this.address = address;
		this.nickname = nickname;
		this.role = role;
	}

}
