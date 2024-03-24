package site.youtogether.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

	public static final String SESSION_COOKIE_NAME = "YTSession";
	public static final int ROOM_CODE_LENGTH = 10;
	public static final int SESSION_CODE_LENGTH = 20;

}
