package site.youtogether.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StartVideoInfoMessage {

	private final MessageType messageType = MessageType.START_VIDEO_INFO;

	private final String videoTitle;
	private final String channelTitle;

}
