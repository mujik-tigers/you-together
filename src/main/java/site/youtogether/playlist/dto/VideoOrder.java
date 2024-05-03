package site.youtogether.playlist.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VideoOrder {

	private final int from;
	private final int to;

}
