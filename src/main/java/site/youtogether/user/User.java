package site.youtogether.user;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;

import lombok.Builder;
import lombok.Getter;

@Document(value = "user")
@Getter
public class User {

	@Id
	private final Long userId;

	private String nickname;
	private Role role;

	@Builder
	public User(Long userId, String nickname, Role role) {
		this.userId = userId;
		this.nickname = nickname;
		this.role = role;
	}

	public void changeNickname(String updateNickname) {
		this.nickname = updateNickname;
	}

}
