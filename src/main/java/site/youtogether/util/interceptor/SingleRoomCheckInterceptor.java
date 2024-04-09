package site.youtogether.util.interceptor;

import java.util.stream.Stream;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.user.infrastructure.UserTrackingStorage;

@Component
@RequiredArgsConstructor
public class SingleRoomCheckInterceptor implements HandlerInterceptor {

	private final CookieProperties cookieProperties;
	private final UserTrackingStorage userTrackingStorage;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (CorsUtils.isPreFlightRequest(request))
			return true;

		String requestMethod = request.getMethod();

		if (!HttpMethod.POST.matches(requestMethod) || request.getCookies() == null) {    // POST가 아니거나 쿠키가 없으면 검증 통과
			return true;
		}

		boolean isAlreadyParticipating = Stream.of(request.getCookies())
			.filter(cookie -> cookie.getName().equals(cookieProperties.getName()))
			.map(cookie -> cookie.getValue())
			.map(cookieValue -> userTrackingStorage.exists(cookieValue))
			.findAny()
			.orElse(false);

		if (isAlreadyParticipating)
			throw new SingleRoomParticipationViolationException();

		return true;
	}

}
