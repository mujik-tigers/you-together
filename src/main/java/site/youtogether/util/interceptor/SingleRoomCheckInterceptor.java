package site.youtogether.util.interceptor;

import static site.youtogether.util.AppConstants.*;

import java.util.stream.Stream;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.youtogether.exception.cookie.CookieInvalidException;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.user.infrastructure.UserTrackingStorage;

@Component
@RequiredArgsConstructor
public class SingleRoomCheckInterceptor implements HandlerInterceptor {

	private final UserTrackingStorage userTrackingStorage;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (CorsUtils.isPreFlightRequest(request))
			return true;

		String requestMethod = request.getMethod();

		if (!HttpMethod.POST.matches(requestMethod) || request.getCookies() == null) {    // POST가 아니거나 쿠키가 없으면 검증 통과
			return true;
		}

		try {
			Stream.of(request.getCookies())
				.filter(cookie -> cookie.getName().equals(SESSION_COOKIE_NAME))
				.map(cookie -> cookie.getValue())
				.map(cookieValue -> userTrackingStorage.findByCookieValue(cookieValue))
				.findAny()
				.ifPresent(userId -> {                                                        // 세션 쿠키가 있고, 대응되는 유저 아이디가 있는 경우
					throw new SingleRoomParticipationViolationException();
				});
		} catch (CookieInvalidException ignored) {
		}        // userTrackingStorage 에서 세션 쿠기 값과 대응되는 유저 아이디가 없다면 발생하는 예외이다.
		return true;
	}

}
