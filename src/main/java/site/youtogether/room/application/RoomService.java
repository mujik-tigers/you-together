package site.youtogether.room.application;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomDetail;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserTrackingStorage;
import site.youtogether.util.RandomUtil;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomStorage roomStorage;
	private final UserTrackingStorage userTrackingStorage;

	public RoomDetail create(String cookieValue, RoomSettings roomSettings, LocalDateTime now) {
		Long userId = userTrackingStorage.save(cookieValue);

		User host = User.builder()
			.userId(userId)
			.nickname(RandomUtil.generateUserNickname())
			.role(Role.HOST)
			.build();

		Room room = Room.builder()
			.capacity(roomSettings.getCapacity())
			.title(roomSettings.getTitle())
			.password(roomSettings.getPassword())
			.createdAt(now)
			.host(host)
			.build();

		roomStorage.save(room);
		return new RoomDetail(room, host);
	}

	public RoomList fetchAll(Pageable pageable, String keyword) {
		Slice<Room> roomSlice = roomStorage.findSliceBy(pageable, keyword);
		return new RoomList(roomSlice);
	}

	public RoomDetail enter(String cookieValue, String roomCode) {
		Long userId = userTrackingStorage.save(cookieValue);

		User user = User.builder()
			.userId(userId)
			.nickname(RandomUtil.generateUserNickname())
			.role(Role.GUEST)
			.build();

		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		room.enterParticipant(user);
		roomStorage.save(room);

		return new RoomDetail(room, user);
	}

	public void leave(String roomCode, Long userId) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		room.leaveParticipant(userId);
		userTrackingStorage.delete(userId);

		roomStorage.save(room);
	}

	public User findParticipant(String roomCode, Long userId) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		return room.findParticipantBy(userId);
	}

}
