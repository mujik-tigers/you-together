package site.youtogether.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

	public static final String SESSION_COOKIE_NAME = "YTSession";
	public static final int ROOM_CODE_LENGTH = 10;
	public static final int SESSION_CODE_LENGTH = 20;
	public static final String STOMP_ENDPOINT = "/ws-stomp";
	public static final String SESSION_CODE = "sessionCode";
	public static final String STOMP_SESSION_NICKNAME = "nickname";
	public static final String STOMP_SESSION_ROOM_CODE = "roomId";

}
