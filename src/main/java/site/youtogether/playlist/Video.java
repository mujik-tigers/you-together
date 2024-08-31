package site.youtogether.playlist;

public record Video(
	String id,
	Long number,
	String thumbnail,
	String title,
	String channelName,
	long duration) {

	public Video {
		// 생성자에서 예외 처리
	}

	public boolean matches(Long number) {
		return this.number.equals(number);
	}

	public boolean doesNotMatch(Long number) {
		return !matches(number);
	}

}
