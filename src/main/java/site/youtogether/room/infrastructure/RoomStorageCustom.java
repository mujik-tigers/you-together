package site.youtogether.room.infrastructure;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import site.youtogether.room.Room;

public interface RoomStorageCustom {

	Slice<Room> findSliceBy(Pageable pageable, String title);

}
