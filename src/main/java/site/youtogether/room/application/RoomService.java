package site.youtogether.room.application;

import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.RoomNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.message.AlarmMessage;
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

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomStorage roomStorage;
	private final PlaylistStorage playlistStorage;
	private final UserStorage userStorage;
	private final MessageService messageService;
	private final RedissonClient redissonClient;

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

	public RoomDetail enterWithLock(Long userId, String roomCode, String passwordInput) {
		RLock lock = redissonClient.getFairLock(roomCode);

		try {
			boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

			if (!available) {
				System.out.println("Lock 획득 실패");
			}

			return enter(userId, roomCode, passwordInput);
		} catch (InterruptedException e) {
			throw new RuntimeException("머 다른 거로도 실패할 수도..");
		} finally {
			lock.unlock();
		}
	}

	public RoomDetail enterWithSpinLock() {
		
	}

	public RoomDetail enter(Long userId, String roomCode, String passwordInput) {
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
		messageService.sendAlarm(new AlarmMessage(room.getCode(), "[알림] 방 제목이 " + newTitle + "(으)로 변경되었습니다."));

		return new ChangedRoomTitle(room);
	}

}
