package site.youtogether.room.infrastructure;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.redis.om.spring.search.stream.EntityStream;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.search.aggr.SortedField.SortOrder;
import site.youtogether.room.Room;
import site.youtogether.room.Room$;

@Component
@RequiredArgsConstructor
public class RoomCustomRepositoryImpl implements RoomCustomRepository {

	private final EntityStream entityStream;

	@Override
	public List<Room> findAllByTitleContaining(String search, Pageable pageable) {
		return entityStream
			.of(Room.class)
			.filter(containsTitle(search))
			.sorted(Room$.CREATED_AT, SortOrder.DESC)
			.skip(pageable.getOffset())
			.limit(pageable.getPageSize())
			.collect(Collectors.toList());
	}

	private SearchFieldPredicate<Room, String> containsTitle(String search) {
		if (search == null || search.isBlank()) {
			return Room$.TITLE.containing("");
		}

		return Room$.TITLE.containing(search.strip());
	}

}
