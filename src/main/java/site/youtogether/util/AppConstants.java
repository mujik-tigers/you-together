package site.youtogether.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

	public static final int ROOM_CODE_LENGTH = 10;
	public static final int COOKIE_VALUE_LENGTH = 20;
	public static final String USER_TRACKING_KEY_PREFIX = "user_tracking:";
	public static final String STOMP_ENDPOINT = "/stomp";
	public static final String USER_ID = "userId";

}
