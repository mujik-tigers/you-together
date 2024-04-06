package site.youtogether.room.infrastructure;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import com.redis.om.spring.search.stream.EntityStream;

import redis.clients.jedis.search.aggr.SortedField;
import site.youtogether.IntegrationTestSupport;
import site.youtogether.room.Room;
import site.youtogether.room.Room$;
import site.youtogether.room.RoomSearchStreamBuilder;
import site.youtogether.user.User;

class RoomStorageTest extends IntegrationTestSupport {

	@Autowired
	private RoomStorage roomStorage;

	@Autowired
	private EntityStream entityStream;

	@AfterEach
	void clear() {
		roomStorage.deleteAll();
	}

	@Test
	@DisplayName("방이 생성된 날짜를 기준으로 정렬할 수 있다")
	void roomSortByCreateDate() throws Exception {
		// given
		Room room1 = createRoom(LocalDateTime.of(2024, 11, 5, 12, 0, 1), "황똥땡의 방");
		Room room2 = createRoom(LocalDateTime.of(2024, 11, 5, 12, 0, 0), "연츠비의 방");
		Room room3 = createRoom(LocalDateTime.of(2025, 11, 3, 11, 0, 0), "연똥땡 방");

		// when
		List<Room> rooms = entityStream
			.of(Room.class)
			.sorted(Room$.CREATE_AT, SortedField.SortOrder.DESC)
			.collect(Collectors.toList());

		// then
		assertThat(rooms).extracting("title").containsExactly(room3.getTitle(), room1.getTitle(), room2.getTitle());
	}

	@Test
	@DisplayName("방의 제목을 사용해 검색할 수 있다")
	void roomSearchByKeyword() throws Exception {
		// given
		Room room1 = createRoom(LocalDateTime.of(2024, 11, 5, 12, 0, 1), "황똥땡의 방");
		Room room2 = createRoom(LocalDateTime.of(2024, 11, 5, 12, 0, 0), "연츠비의 방");
		Room room3 = createRoom(LocalDateTime.of(2025, 11, 3, 11, 0, 0), "연똥땡의 방");

		String keyword = "똥땡";

		// when
		List<Room> rooms = entityStream
			.of(Room.class)
			.filter(Room$.TITLE.containing(keyword))
			.collect(Collectors.toList());

		// then
		assertThat(rooms).extracting("title").containsExactlyInAnyOrder(room1.getTitle(), room3.getTitle());
	}

	@Test
	@DisplayName("알파벳 한 글자로도 방의 제목을 검색할 수 있다")
	void roomSearchByOneLetter() throws Exception {
		// given
		Room room1 = createRoom(LocalDateTime.of(2024, 11, 5, 12, 0, 1), "hyun's room");
		Room room2 = createRoom(LocalDateTime.of(2024, 11, 5, 12, 0, 0), "yeon's room");
		Room room3 = createRoom(LocalDateTime.of(2025, 11, 3, 11, 0, 0), "hyeonise");

		String keyword = "h";

		// when
		List<Room> rooms = entityStream
			.of(Room.class)
			.filter(Room$.TITLE.containing(keyword))
			.collect(Collectors.toList());

		// then
		assertThat(rooms).extracting("title").containsExactlyInAnyOrder(room1.getTitle(), room3.getTitle());
	}

	@Test
	@DisplayName("검색 키워드가 없는 경우와 있는 경우에 적절하게 맞춰 동적 쿼리를 만든다")
	void dynamicQuery() throws Exception {
		// given
		Room room1 = createRoom(LocalDateTime.of(2024, 1, 5, 12, 0, 1), "황똥땡1의 방");
		Room room2 = createRoom(LocalDateTime.of(2024, 2, 5, 12, 0, 0), "황똥땡2의 방");
		Room room3 = createRoom(LocalDateTime.of(2024, 3, 3, 11, 0, 0), "황똥땡3의 방");
		Room room4 = createRoom(LocalDateTime.of(2024, 4, 5, 12, 0, 1), "황똥땡4의 방");
		Room room5 = createRoom(LocalDateTime.of(2024, 5, 5, 12, 0, 0), "황똥땡5의 방");
		Room room6 = createRoom(LocalDateTime.of(2024, 6, 3, 11, 0, 0), "황똥땡6의 방");

		String keyword1 = null;
		String keyword3 = "3";

		PageRequest pageable = PageRequest.of(0, 3);

		// when

		Slice<Room> slice1 = new RoomSearchStreamBuilder(entityStream)
			.filterTitleContaining(keyword1)
			.sortByDateDesc()
			.buildSlice(pageable);

		Slice<Room> slice3 = new RoomSearchStreamBuilder(entityStream)
			.filterTitleContaining(keyword3)
			.sortByDateDesc()
			.buildSlice(pageable);

		// then
		assertThat(slice1.getContent()).extracting("title").containsExactly(
			room6.getTitle(), room5.getTitle(), room4.getTitle()
		);
		assertThat(slice1.hasNext()).isTrue();

		assertThat(slice3.getContent()).extracting("title").containsExactly(room3.getTitle());
		assertThat(slice3.hasNext()).isFalse();
	}

	private Room createRoom(LocalDateTime createTime, String title) {
		User user = User.builder()
			.sessionCode("daflkjsd")
			.build();

		Room room = Room.builder()
			.title(title)
			.host(user)
			.createAt(createTime)
			.build();

		roomStorage.save(room);

		return room;
	}

}
