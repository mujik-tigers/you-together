package site.youtogether.room.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import site.youtogether.room.Room;

@Component
public class RoomStorage {

	private final Map<String, Room> roomStorage = new ConcurrentHashMap<>();

	public boolean existsByAddress(String address) {
		return roomStorage.containsKey(address);
	}

}
