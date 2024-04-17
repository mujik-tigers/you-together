package site.youtogether.user;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Getter;
import site.youtogether.exception.user.HigherOrEqualRoleChangeException;
import site.youtogether.exception.user.HigherOrEqualRoleUserChangeException;
import site.youtogether.exception.user.NotManageableUserException;
import site.youtogether.exception.user.SelfRoleChangeException;

@Getter
public class User {

	@Id
	private final Long userId;

	private String nickname;
	private Role role;

	@Builder
	private User(Long userId, String nickname, Role role) {
		this.userId = userId;
		this.nickname = nickname;
		this.role = role;
	}

	public void changeNickname(String updateNickname) {
		nickname = updateNickname;
	}

	public boolean hasLowerOrEqualRoleThan(Role compareRole) {
		return role.isLowerOrEqualThan(compareRole);
	}

	public void changeRole(Role changeRole) {
		role = changeRole;
	}

	public boolean isNotManageable() {
		return role.isLowerThan(Role.MANAGER);
	}

	public boolean isViewer() {
		return role == Role.VIEWER;
	}

	public boolean isHost() {
		return role == Role.HOST;
	}

	public User changeOtherUserRole(User changedUser, Role changeRole) {
		if (userId.equals(changedUser.getUserId())) {
			throw new SelfRoleChangeException();
		}

		if (isNotManageable()) {
			throw new NotManageableUserException();
		}

		if (hasLowerOrEqualRoleThan(changedUser.getRole())) {
			throw new HigherOrEqualRoleUserChangeException();
		}

		if (hasLowerOrEqualRoleThan(changeRole)) {
			throw new HigherOrEqualRoleChangeException();
		}

		changedUser.changeRole(changeRole);
		return changedUser;
	}

}
