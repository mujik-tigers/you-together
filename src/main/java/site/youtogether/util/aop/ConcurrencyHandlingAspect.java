package site.youtogether.util.aop;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.exception.CustomException;
import site.youtogether.exception.playlist.PlaylistLockAcquisitionFailureException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.playlist.dto.VideoOrder;
import site.youtogether.user.User;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.user.infrastructure.UserStorage;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ConcurrencyHandlingAspect {

	private final RedissonClient redissonClient;
	private final UserStorage userStorage;

	@Around("@annotation(RoomSynchronize) && args(roomCode, ..)")
	public Object updateRoom(ProceedingJoinPoint joinPoint, String roomCode) {
		RLock lock = redissonClient.getLock(roomCode);
		return synchronize(joinPoint, lock);
	}

	@Around("@annotation(RoomSynchronize) && args(userId, ..)")
	public Object updateRoom(ProceedingJoinPoint joinPoint, Long userId) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		RLock lock = redissonClient.getLock(user.getCurrentRoomCode());
		return synchronize(joinPoint, lock);
	}

	@Around("@annotation(UserSynchronize) && args(userId, form)")
	public Object updateUser(ProceedingJoinPoint joinPoint, Long userId, UserRoleChangeForm form) {
		RLock lock = redissonClient.getLock(String.valueOf(form.getTargetUserId()));
		return synchronize(joinPoint, lock);
	}

	@Around("@annotation(PlaylistSynchronize) && args(userId, ..)")
	public void updatePlaylist(ProceedingJoinPoint joinPoint, Long userId) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		RLock lock = redissonClient.getLock("pl-" + user.getCurrentRoomCode());
		synchronize(joinPoint, lock);
	}

	@Around("@annotation(PlaylistSynchronize) && args(userId, videoOrder)")
	public void updatePlaylist(ProceedingJoinPoint joinPoint, Long userId, VideoOrder videoOrder) {
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);
		RLock lock = redissonClient.getLock("pl-" + user.getCurrentRoomCode());
		try {
			boolean available = lock.tryLock();                                    // 플레이리스트 업데이트 시엔, 락을 획득하지 못하면 바로 실패 처리
			if (!available) {
				throw new PlaylistLockAcquisitionFailureException();
			}
			joinPoint.proceed();
		} catch (CustomException e) {
			throw e;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			lock.unlock();
		}
	}

	private Object synchronize(ProceedingJoinPoint joinPoint, RLock lock) {
		try {
			boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
			if (!available) {
				throw new RuntimeException("Lock 획득 실패");
			}
			return joinPoint.proceed();
		} catch (CustomException e) {
			throw e;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			lock.unlock();
		}
	}

}
