package site.youtogether.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

	public static final String SESSION_CODE_KEY_PREFIX = "sessionCode:";
	public static final int ROOM_CODE_LENGTH = 10;
	public static final int COOKIE_VALUE_LENGTH = 20;

}
