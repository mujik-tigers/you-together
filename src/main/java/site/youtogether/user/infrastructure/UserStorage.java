package site.youtogether.user.infrastructure;

import java.util.List;

import com.redis.om.spring.repository.RedisDocumentRepository;

import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.user.User;

public interface UserStorage extends RedisDocumentRepository<User, Long> {

	default User getById(Long id) {
		return findById(id).orElseThrow(UserNoExistenceException::new);
	}

	List<User> findAllByCurrentRoomCode(String currentRoomCode);

}
