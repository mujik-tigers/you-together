package site.youtogether.message;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.youtogether.playlist.dto.VideoInfo;

@RequiredArgsConstructor
@Getter
public class PlaylistMessage {

	private final MessageType messageType = MessageType.PLAYLIST;

	private final List<VideoInfo> playlist;

}
