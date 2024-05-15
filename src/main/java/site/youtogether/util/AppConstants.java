package site.youtogether.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

	public static final int ROOM_CODE_LENGTH = 10;
	public static final int USER_HISTORY_LENGTH = 10;
	public static final String STOMP_ENDPOINT = "/stomp";
	public static final String USER_ID = "userId";
	public static final String ROOM_CODE = "roomCode";
	public static final String CHAT_PREFIX = "chat:";
	public static final String SUBSCRIBE_PATH = "/sub/messages/rooms/";

}
