package site.youtogether.user;

public enum Role {

	HOST(5), MANAGER(4), EDITOR(3), GUEST(2), VIEWER(1);

	private final int weight;

	Role(int weight) {
		this.weight = weight;
	}

	public boolean isLowerOrEqualThan(Role compareRole) {
		return this.weight <= compareRole.weight;
	}

}
