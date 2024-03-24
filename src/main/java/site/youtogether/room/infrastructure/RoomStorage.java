package site.youtogether.room.infrastructure;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import site.youtogether.room.Room;

@Repository
public interface RoomStorage extends CrudRepository<Room, String> {

	Slice<Room> findAll(Pageable pageable);

}
