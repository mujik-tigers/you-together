package site.youtogether.room.infrastructure;

import java.util.List;

import org.springframework.data.domain.Pageable;

import site.youtogether.room.Room;

public interface RoomCustomRepository {

	List<Room> findAllByTitleContaining(String title, Pageable pageable);

}
