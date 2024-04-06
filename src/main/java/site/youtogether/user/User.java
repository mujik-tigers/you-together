package site.youtogether.user;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;

import lombok.Builder;
import lombok.Getter;

@Document(value = "user")
@Getter
public class User {

	@Id
	private final String sessionCode;

	private final String nickname;
	private final Role role;

	@Builder
	public User(String sessionCode, String nickname, Role role) {
		this.sessionCode = sessionCode;
		this.nickname = nickname;
		this.role = role;
	}

}
