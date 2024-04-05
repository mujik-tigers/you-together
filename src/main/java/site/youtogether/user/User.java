package site.youtogether.user;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;

import lombok.Builder;
import lombok.Getter;

@Getter
@Document(value = "user")
public class User {

	@Id
	private final String sessionCode;

	private final String address;
	private final String nickname;
	private final Role role;

	@Builder
	public User(String sessionCode, String address, String nickname, Role role) {
		this.sessionCode = sessionCode;
		this.address = address;
		this.nickname = nickname;
		this.role = role;
	}

}
