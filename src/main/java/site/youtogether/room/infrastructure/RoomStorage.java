package site.youtogether.room.infrastructure;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import site.youtogether.room.Room;

@Repository
public interface RoomStorage extends CrudRepository<Room, String> {

}
