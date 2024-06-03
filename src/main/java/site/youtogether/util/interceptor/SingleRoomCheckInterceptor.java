package site.youtogether.util.interceptor;

import static site.youtogether.util.AppConstants.*;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

@Component
@RequiredArgsConstructor
@Slf4j
public class SingleRoomCheckInterceptor implements HandlerInterceptor {

	private final UserStorage userStorage;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (CorsUtils.isPreFlightRequest(request))
			return true;

		String requestMethod = request.getMethod();
		if (!HttpMethod.POST.matches(requestMethod)) {
			return true;
		}

		Long userId = (Long)request.getAttribute(USER_ID);
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		if (user.isParticipant()) {
			log.info("--USER ID {} - {} 방 입장 인터셉터에서 실패함--", userId, request.getRequestURI());
			throw new SingleRoomParticipationViolationException();
		}

		log.info("--USER ID {} - {} 방 입장 인터셉터 통과함--", userId, request.getRequestURI());
		return true;
	}

}
