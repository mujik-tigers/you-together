package site.youtogether.video;

import lombok.Builder;

public record Video(String id, String title, String channel, String thumbnail) {

	@Builder
	public Video {
	}

}
