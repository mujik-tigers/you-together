package site.youtogether.user;

import static site.youtogether.util.AppConstants.*;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.exception.user.HigherOrEqualRoleChangeException;
import site.youtogether.exception.user.HigherOrEqualRoleUserChangeException;
import site.youtogether.exception.user.NotManageableUserException;
import site.youtogether.exception.user.SelfRoleChangeException;
import site.youtogether.exception.user.UserNotEnteringException;
import site.youtogether.exception.user.UsersInDifferentRoomException;

@Document(value = "user")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class User {

	@Id
	private Long id;

	private String nickname;

	@Indexed
	private String currentRoomCode;

	@Indexed
	private boolean activate;

	private final Map<String, Role> roleHistory = new HashMap<>();
	private final Queue<String> roomCodeQueue = new ArrayDeque<>();

	public static class Builder {
		private final Long id;
		private final String nickname;

		private String currentRoomCode = null;
		private boolean activate = false;

		public Builder(Long id, String nickname) {
			this.id = id;
			this.nickname = nickname;
		}

		public Builder currentRoomCode(String val) {
			currentRoomCode = val;
			return this;
		}

		public Builder activate(boolean val) {
			activate = val;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}

	private User(Builder builder) {
		id = builder.id;
		nickname = builder.nickname;
		currentRoomCode = builder.currentRoomCode;
		activate = builder.activate;
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
			roleHistory.put(roomCode, Role.GUEST);
			if (roomCodeQueue.size() >= USER_HISTORY_LENGTH) {
				removeOldestRoomCode();
			}
		} else {
			roomCodeQueue.remove(roomCode);
		}
		roomCodeQueue.offer(roomCode);
		currentRoomCode = roomCode;
		activate = true;
	}

	public void createRoom(String createRoomCode) {
		roleHistory.put(createRoomCode, Role.HOST);
	}

	public boolean isNotEditable() {
		return getRoleInCurrentRoom().isLowerThan(Role.EDITOR);
	}

	public void leaveRoom() {
		currentRoomCode = null;
	}

	public Role getRoleInCurrentRoom() {
		return roleHistory.get(getCurrentRoomCode());
	}

	private boolean hasLowerRoleThan(Role compareRole) {
		Role role = getRoleInCurrentRoom();
		return role.isLowerThan(compareRole);
	}

	private boolean isFirstTimeEntering(String roomCode) {
		return !roleHistory.containsKey(roomCode);
	}

	private void removeOldestRoomCode() {
		String deletedRoomCode = roomCodeQueue.poll();
		roleHistory.remove(deletedRoomCode);
	}

	private boolean isInSameRoom(User user, User targetUser) {
		return user.getCurrentRoomCode().equals(targetUser.getCurrentRoomCode());
	}

	private void changeRole(Role changeRole) {
		roleHistory.put(getCurrentRoomCode(), changeRole);
	}

}
