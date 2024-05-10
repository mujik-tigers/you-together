package site.youtogether.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.exception.user.HigherOrEqualRoleChangeException;
import site.youtogether.exception.user.HigherOrEqualRoleUserChangeException;
import site.youtogether.exception.user.NotManageableUserException;
import site.youtogether.exception.user.SelfRoleChangeException;
import site.youtogether.exception.user.UserNotEnteringException;
import site.youtogether.exception.user.UsersInDifferentRoomException;

@Document(value = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

	@Id
	private Long id;

	@Indexed
	private String currentRoomCode;

	@Indexed
	private boolean activate;

	private String nickname;
	private Map<String, Role> history = new HashMap<>();

	@Builder
	private User(Long id, String nickname, String currentRoomCode, boolean activate) {
		this.id = id;
		this.nickname = nickname;
		this.currentRoomCode = currentRoomCode;
		this.activate = activate;
	}

	public String getCurrentRoomCode() {
		return Optional.ofNullable(currentRoomCode)
			.orElseThrow(UserNotEnteringException::new);
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

	public void changeOtherUserRole(User targetUser, Role newUserRole) {
		if (id.equals(targetUser.getId())) {
			throw new SelfRoleChangeException();
		}

		if (!isInSameRoom(this, targetUser)) {
			throw new UsersInDifferentRoomException();
		}

		if (isNotManageable()) {
			throw new NotManageableUserException();
		}

		if (hasLowerOrEqualRoleThan(targetUser.getRoleInCurrentRoom())) {
			throw new HigherOrEqualRoleUserChangeException();
		}

		if (hasLowerRoleThan(newUserRole)) {
			throw new HigherOrEqualRoleChangeException();
		}

		targetUser.changeRole(newUserRole);
	}

	public void enterRoom(String roomCode) {
		if (isFirstTimeEntering(roomCode)) {
			history.put(roomCode, Role.GUEST);
		}
		currentRoomCode = roomCode;
		activate = true;
	}

	public void createRoom(String createRoomCode) {
		history.put(createRoomCode, Role.HOST);
	}

	public boolean isNotEditable() {
		return getRoleInCurrentRoom().isLowerThan(Role.EDITOR);
	}

	public void leaveRoom() {
		currentRoomCode = null;
	}

	public Role getRoleInCurrentRoom() {
		return history.get(getCurrentRoomCode());
	}

	private boolean hasLowerRoleThan(Role compareRole) {
		Role role = getRoleInCurrentRoom();
		return role.isLowerThan(compareRole);
	}

	private boolean isFirstTimeEntering(String roomCode) {
		return !history.containsKey(roomCode);
	}

	private boolean isInSameRoom(User user, User targetUser) {
		return user.getCurrentRoomCode().equals(targetUser.getCurrentRoomCode());
	}

	private void changeRole(Role changeRole) {
		history.put(getCurrentRoomCode(), changeRole);
	}

}
