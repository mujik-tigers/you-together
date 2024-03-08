package site.youtogether.room.infrastructure;

import org.springframework.data.repository.CrudRepository;

import site.youtogether.room.Room;

public interface RoomRepository extends CrudRepository<Room, String> {
}
