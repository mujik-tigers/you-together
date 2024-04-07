package site.youtogether.user.infrastructure;

import static site.youtogether.util.AppConstants.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import site.youtogether.util.RandomUtil;

@Repository
@RequiredArgsConstructor
public class UserTrackingStorage {

	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * 유저 아이디를 랜덤으로 만들고 쿠키값에 대응되게 저장한다.
	 *
	 * @param cookieValue 클라이언트로부터 받은 쿠키값
	 * @return userId
	 */
	public String save(String cookieValue) {
		String userId = RandomUtil.generateRandomCode(10);
		redisTemplate.opsForValue()
			.set(USER_TRACKING_KEY_PREFIX + cookieValue, userId);
		return userId;
	}

	/**
	 * 클라이언트로부터 받은 쿠키값을 바탕으로 대응되는 유저 아이디를 찾는다.
	 *
	 * @param cookieValue 클라이언트로부터 받은 쿠키값
	 * @return userId
	 */
	public String findByCookieValue(String cookieValue) {
		return redisTemplate.opsForValue()
			.get(USER_TRACKING_KEY_PREFIX + cookieValue);
	}

}
