package site.youtogether.user;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.exception.user.HigherOrEqualRoleChangeException;
import site.youtogether.exception.user.HigherOrEqualRoleUserChangeException;
import site.youtogether.exception.user.NotManageableUserException;
import site.youtogether.exception.user.SelfRoleChangeException;

@Document(value = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

	@Id
	private Long id;

	private String nickname;
	private Role role;
	private String currentRoomCode;
	private String previousRoomCode;

	@Builder
	private User(Long id, String nickname, Role role, String currentRoomCode, String previousRoomCode) {
		this.id = id;
		this.nickname = nickname;
		this.role = role;
		this.currentRoomCode = currentRoomCode;
		this.previousRoomCode = previousRoomCode;
	}

	public boolean isParticipant() {
		return currentRoomCode != null;
	}

	public boolean isFirstTimeEntering(String roomCode) {
		return !roomCode.equals(previousRoomCode);
	}

	public boolean isHost() {
		return role == Role.HOST;
	}

	public boolean isViewer() {
		return role == Role.VIEWER;
	}

	public boolean isNotManageable() {
		return role.isLowerThan(Role.MANAGER);
	}

	public boolean hasLowerOrEqualRoleThan(Role compareRole) {
		return role.isLowerOrEqualThan(compareRole);
	}

	public int getPriority() {
		return role.getPriority();
	}

	public void changeNickname(String updateNickname) {
		nickname = updateNickname;
	}

	public void changeRole(Role changeRole) {
		role = changeRole;
	}

	public User changeOtherUserRole(User changedUser, Role changeRole) {
		if (id.equals(changedUser.getId())) {
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

	public void enterRoom(String roomCode) {
		currentRoomCode = roomCode;
	}

	public void leaveRoom() {
		previousRoomCode = currentRoomCode;
		currentRoomCode = null;
	}

}
