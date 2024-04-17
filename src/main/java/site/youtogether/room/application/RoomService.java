package site.youtogether.room.application;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomEmptyException;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.message.application.MessageService;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomDetail;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.dto.UpdatedRoomTitle;
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
	private final MessageService messageService;

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

	public RoomDetail enter(String cookieValue, String roomCode, String passwordInput) {
		Long userId = userTrackingStorage.save(cookieValue);        // TODO: 트랜잭션 안되서, enter 실패 시, userTrackingStorage 에 실패한 데이터가 쌓임.

		User user = User.builder()
			.userId(userId)
			.nickname(RandomUtil.generateUserNickname())
			.role(Role.GUEST)
			.build();

		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		room.enterParticipant(user, passwordInput);
		roomStorage.save(room);

		return new RoomDetail(room, user);
	}

	public void leave(String roomCode, Long userId, String cookieValue) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		userTrackingStorage.delete(cookieValue);

		try {
			room.leaveParticipant(userId);
		} catch (RoomEmptyException e) {
			roomStorage.deleteById(roomCode);
			return;
		}
		roomStorage.save(room);
	}

	public User findParticipant(String roomCode, Long userId) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		return room.findParticipantBy(userId);
	}

	public UpdatedRoomTitle changeRoomTitle(Long userId, String roomCode, String updateTitle) {
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);
		room.changeRoomTitle(userId, updateTitle);
		roomStorage.save(room);

		messageService.sendRoomTitle(roomCode);
		return new UpdatedRoomTitle(room);
	}

}
