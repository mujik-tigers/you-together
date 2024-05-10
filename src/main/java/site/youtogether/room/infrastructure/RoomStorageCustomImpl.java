package site.youtogether.room.infrastructure;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.redis.om.spring.search.stream.EntityStream;

import lombok.RequiredArgsConstructor;
import site.youtogether.room.Room;

@Component
@RequiredArgsConstructor
public class RoomStorageCustomImpl implements RoomStorageCustom {

	private final EntityStream entityStream;

	@Override
	public Slice<Room> findSliceBy(Pageable pageable, String keyword) {
		return new RoomSearchStreamBuilder(entityStream)
			.filterTitleContaining(keyword)
			.filterNoParticipant()
			.sortByCreatedAtDesc()
			.buildSlice(pageable);
	}

}
