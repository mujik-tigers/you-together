package site.youtogether.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

	public static final String HOSTING_KEY = "hosting";
	public static final String WATCHING_KEY = "watching";
	public static final String ROOM_KEY_PREFIX = "room:";
	public static final String PARTICIPANTS_KEY_PREFIX = "participants:";
	public static final String USER_KEY_PREFIX = "user:";
	public static final long day = 86_400;

}
