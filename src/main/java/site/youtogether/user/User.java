package site.youtogether.user;

import java.util.HashMap;
import java.util.Map;

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
import site.youtogether.exception.user.UsersInDifferentRoomException;

@Document(value = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

	@Id
	private Long id;

	private String nickname;
	private String currentRoomCode;
	private Map<String, Role> history = new HashMap<>();

	@Builder
	private User(Long id, String nickname, String currentRoomCode) {
		this.id = id;
		this.nickname = nickname;
		this.currentRoomCode = currentRoomCode;
	}

	public boolean isParticipant() {
		return currentRoomCode != null;
	}

	public boolean isHost() {
		return getRoleInCurrentRoom() == Role.HOST;
	}

	public boolean isViewer() {
		return getRoleInCurrentRoom() == Role.VIEWER;
	}

	public boolean isNotManageable() {
		Role role = getRoleInCurrentRoom();
		return role.isLowerThan(Role.MANAGER);
	}

	public boolean hasLowerOrEqualRoleThan(Role compareRole) {
		Role role = getRoleInCurrentRoom();
		return role.isLowerOrEqualThan(compareRole);
	}

	public void changeNickname(String updateNickname) {
		nickname = updateNickname;
	}

	public void changeOtherUserRole(String roomCode, User targetUser, Role newUserRole) {
		if (id.equals(targetUser.getId())) {
			throw new SelfRoleChangeException();
		}

		if (!isInSameRoom(this, targetUser, roomCode)) {
			throw new UsersInDifferentRoomException();
		}

		if (isNotManageable()) {
			throw new NotManageableUserException();
		}

		if (hasLowerOrEqualRoleThan(targetUser.getRoleInCurrentRoom())) {
			throw new HigherOrEqualRoleUserChangeException();
		}

		if (hasLowerOrEqualRoleThan(newUserRole)) {
			throw new HigherOrEqualRoleChangeException();
		}

		targetUser.changeRole(roomCode, newUserRole);
	}

	public void enterRoom(String roomCode) {
		if (isFirstTimeEntering(roomCode)) {
			history.put(roomCode, Role.GUEST);
		}
		currentRoomCode = roomCode;
	}

	public void createRoom(String createRoomCode) {
		history.put(createRoomCode, Role.HOST);
		currentRoomCode = createRoomCode;
	}

	public void leaveRoom() {
		currentRoomCode = null;
	}

	public Role getRoleInCurrentRoom() {
		return history.get(currentRoomCode);
	}

	private boolean isFirstTimeEntering(String roomCode) {
		return !history.containsKey(roomCode);
	}

	private boolean isInSameRoom(User user, User targetUser, String roomCode) {
		return user.getCurrentRoomCode().equals(roomCode) && targetUser.getCurrentRoomCode().equals(roomCode);
	}

	private void changeRole(String roomCode, Role changeRole) {
		history.put(roomCode, changeRole);
	}

}
