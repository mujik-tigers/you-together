package site.youtogether.util;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

	public static final int ROOM_CODE_LENGTH = 10;
	public static final int COOKIE_VALUE_LENGTH = 20;
	public static final String USER_TRACKING_GROUP = "user_tracking_group";
	public static final Duration TTL = Duration.ofDays(1);
	public static final String STOMP_ENDPOINT = "/stomp";
	public static final String USER_ID = "userId";
	public static final String ROOM_CODE = "roomCode";
	public static final String SESSION_COOKIE = "sessionCookie";
	public static final String BEARER = "Bearer ";

}
