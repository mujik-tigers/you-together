package site.youtogether.learn;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.youtogether.IntegrationTestSupport;

public class SpringRedisOMTest extends IntegrationTestSupport {

	@Autowired
	private MockDataRepository mockDataRepository;

	@AfterEach
	void clear() {
		mockDataRepository.deleteAll();
	}

	@Test
	@DisplayName("간단한 CRUD")
	void simpleCRUD() throws Exception {
		MockData mockData1 = new MockData("ghkdgus29", "황똥땡", "위대한 황똥땡", "연똥땡");
		mockDataRepository.save(mockData1);

		MockData mockData2 = new MockData("yeonise", "연똥땡", "위대한 연츠비", "연츠비");
		mockDataRepository.save(mockData2);

		List<MockData> all = mockDataRepository.findAll();
		assertThat(all).usingRecursiveFieldByFieldElementComparator().isEqualTo(List.of(mockData1, mockData2));

		mockDataRepository.deleteAll();
		List<MockData> empty = mockDataRepository.findAll();
		assertThat(empty).isEmpty();
	}

}
