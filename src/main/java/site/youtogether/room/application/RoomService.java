package site.youtogether.room.application;

import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.AlarmMessage;
import site.youtogether.message.ChatHistory;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.Playlist;
import site.youtogether.playlist.infrastructure.PlaylistStorage;
import site.youtogether.room.Room;
import site.youtogether.room.dto.ChangedRoomTitle;
import site.youtogether.room.dto.NewRoom;
import site.youtogether.room.dto.RoomDetail;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.infrastructure.RoomStorage;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;
import site.youtogether.util.aop.RoomSynchronize;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomStorage roomStorage;
	private final PlaylistStorage playlistStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;
	private final RedisTemplate<String, ChatHistory> chatRedisTemplate;

	public NewRoom create(Long userId, RoomSettings roomSettings, LocalDateTime now) {
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
			.activate(true)
			.build();
		roomStorage.save(room);

		Playlist playlist = new Playlist(roomCode);
		playlistStorage.save(playlist);

		return new NewRoom(roomCode, room.getPassword());
	}

	public RoomList fetchAll(Pageable pageable, String keyword) {
		Slice<Room> roomSlice = roomStorage.findSliceBy(pageable, keyword);

		return new RoomList(roomSlice);
	}

	@RoomSynchronize
	public RoomDetail enter(String roomCode, Long userId, String passwordInput) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		Room room = roomStorage.findById(roomCode)
			.orElseThrow(RoomNoExistenceException::new);

		user.enterRoom(roomCode);
		room.enter(passwordInput);

		userStorage.save(user);
		roomStorage.save(room);

		return new RoomDetail(room, user);
	}

	@RoomSynchronize
	public void leave(Long userId) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		Room room = roomStorage.findById(user.getCurrentRoomCode())
			.orElseThrow(RoomNoExistenceException::new);

		user.leaveRoom();
		room.leave();

		userStorage.save(user);
		roomStorage.save(room);
	}

	public ChangedRoomTitle changeRoomTitle(Long userId, String newTitle) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		Room room = roomStorage.findById(user.getCurrentRoomCode())
			.orElseThrow(RoomNoExistenceException::new);
		room.changeTitle(user, newTitle);
		roomStorage.save(room);

		messageService.sendRoomTitle(user.getCurrentRoomCode());
		messageService.sendAlarm(new AlarmMessage(RandomUtil.generateChatId(), room.getCode(), "방 제목이 " + newTitle + "(으)로 변경되었습니다."));

		return new ChangedRoomTitle(room);
	}

}
