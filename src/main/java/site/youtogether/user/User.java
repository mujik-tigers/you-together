package site.youtogether.user;

import lombok.Getter;

@Getter
public class User {

	private final String ip;
	private String name;

	public User(String ip) {
		this.ip = ip;
		this.name = "황똥땡" + (int)(Math.random() * 1000);
	}

}
