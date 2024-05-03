package site.youtogether.user.infrastructure;

import java.util.List;

import com.redis.om.spring.repository.RedisDocumentRepository;

import site.youtogether.user.User;

public interface UserStorage extends RedisDocumentRepository<User, Long> {

	List<User> findAllByCurrentRoomCode(String currentRoomCode);

}
