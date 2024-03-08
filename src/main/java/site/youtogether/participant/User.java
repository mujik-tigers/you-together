package site.youtogether.participant;

import java.util.Random;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

	private final String userIp;
	private String userName;
	private Role userRole;

	@Builder
	private User(String userIp, Role userRole) {
		this.userIp = userIp;
		this.userName = generateRandomUserName();
		this.userRole = userRole;
	}

	private String generateRandomUserName() {
		return "황똥땡" + new Random().nextInt(100);
	}

}
