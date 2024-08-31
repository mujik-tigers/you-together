package site.youtogether.player.handler;

public enum PlayerState {

	PLAY(new PlayStateHandler()),
	PAUSE(new PauseStateHandler()),
	END(new EndStateHandler()),
	RATE(new RateStateHandler());

	private final PlayerStateHandler handler;

	PlayerState(PlayerStateHandler handler) {
		this.handler = handler;
	}

	public static PlayerStateHandler handlerOf(PlayerState playerState) {
		return playerState.handler;
	}

}
