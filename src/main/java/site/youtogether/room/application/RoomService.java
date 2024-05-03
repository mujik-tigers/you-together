package site.youtogether.room.application;

import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.room.Room;
import site.youtogether.room.dto.ChangedRoomTitle;
import site.youtogether.room.dto.RoomDetail;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomStorage roomStorage;
	private final PlaylistStorage playlistStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;

	public RoomDetail create(Long userId, RoomSettings roomSettings, LocalDateTime now) {
		String roomCode = RandomUtil.generateRandomCode(ROOM_CODE_LENGTH);

		User host = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		host.createRoom(roomCode);
		userStorage.save(host);

		Room room = Room.builder()
			.code(roomCode)
			.capacity(roomSettings.getCapacity())
			.title(roomSettings.getTitle())
			.password(roomSettings.getPassword())
			.createdAt(now)
			.build();
		roomStorage.save(room);

		Playlist playlist = new Playlist(roomCode);
		playlistStorage.save(playlist);

		return new RoomDetail(room, host);
	}

	public RoomList fetchAll(Pageable pageable, String keyword) {
		Slice<Room> roomSlice = roomStorage.findSliceBy(pageable, keyword);
		return new RoomList(roomSlice);
	}

	public RoomDetail enter(Long userId, String roomCode, String passwordInput) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		user.enterRoom(roomCode);
		userStorage.save(user);

		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);
		room.enter(passwordInput);
		roomStorage.save(room);

		return new RoomDetail(room, user);
	}

	public void leave(String roomCode, Long userId) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		user.leaveRoom();
		userStorage.save(user);

		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);
		room.leave();
		roomStorage.save(room);
	}

	public ChangedRoomTitle changeRoomTitle(Long userId, String roomCode, String updateTitle) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);
		room.changeTitle(user, updateTitle);
		roomStorage.save(room);

		messageService.sendRoomTitle(roomCode);

		return new ChangedRoomTitle(room);
	}

}
