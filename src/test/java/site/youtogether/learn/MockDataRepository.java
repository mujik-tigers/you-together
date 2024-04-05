package site.youtogether.learn;

import java.util.Optional;

import com.redis.om.spring.repository.RedisDocumentRepository;

public interface MockDataRepository extends RedisDocumentRepository<MockData, String> {

	Optional<MockData> findByTitle(String title);

}
