package site.youtogether.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

	public static final String PARTICIPANTS_KEY = "participants";
	public static final String ROOM_KEY_PREFIX = "room:";
	public static final String USER_KEY_PREFIX = "user:";
	public static final String SESSION_COOKIE_NAME = "YTSession";
	public static final int ROOM_CODE_LENGTH = 10;
	public static final int SESSION_CODE_LENGTH = 20;

}
