package site.youtogether.room;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

import com.redis.om.spring.search.stream.EntityStream;
import com.redis.om.spring.search.stream.SearchStream;

import redis.clients.jedis.search.aggr.SortedField;

public class RoomSearchStreamBuilder {

	private SearchStream<Room> roomSearchStream;

	public RoomSearchStreamBuilder(EntityStream entityStream) {
		this.roomSearchStream = entityStream.of(Room.class);
	}

	public RoomSearchStreamBuilder filterTitleContaining(String keyword) {
		if (StringUtils.hasText(keyword)) {
			roomSearchStream = roomSearchStream.filter(Room$.TITLE.containing(keyword));
		}
		return this;
	}

	public RoomSearchStreamBuilder sortByDateDesc() {
		roomSearchStream = roomSearchStream.sorted(Room$.CREATE_AT, SortedField.SortOrder.DESC);
		return this;
	}

	public Slice<Room> buildSlice(Pageable pageable) {
		List<Room> content = roomSearchStream.skip(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.collect(Collectors.toList());

		boolean hasNext = false;
		if (content.size() == pageable.getPageSize() + 1) {
			hasNext = true;
			content.remove(content.size() - 1);
		}

		return new SliceImpl<Room>(content, pageable, hasNext);
	}

}
