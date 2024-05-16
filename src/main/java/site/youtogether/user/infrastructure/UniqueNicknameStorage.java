package site.youtogether.user.infrastructure;

import static site.youtogether.util.AppConstants.*;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.UserNicknameDuplicateException;

@Repository
@RequiredArgsConstructor
public class UniqueNicknameStorage {

	private final StringRedisTemplate redisTemplate;
	private final RedisScript<Boolean> updateUniqueNicknameScript;

	public boolean exist(String nickname) {
		return redisTemplate.opsForSet().isMember(USER_NICKNAME_SET, nickname);
	}

	public void save(String nickname) {
		redisTemplate.opsForSet().add(USER_NICKNAME_SET, nickname);
	}

	public void update(String oldNickname, String newNickname) {
		Boolean updateResult = redisTemplate.execute(updateUniqueNicknameScript, List.of(USER_NICKNAME_SET), oldNickname, newNickname);

		if (!updateResult) {
			throw new UserNicknameDuplicateException();
		}
	}

	public void delete() {
		redisTemplate.delete(USER_NICKNAME_SET);
	}

}
