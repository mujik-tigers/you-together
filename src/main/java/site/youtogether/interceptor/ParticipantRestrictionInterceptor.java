package site.youtogether.interceptor;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.user.infrastructure.UserTrackingStorage;

@Component
@RequiredArgsConstructor
public class ParticipantRestrictionInterceptor implements HandlerInterceptor {

	private final CookieProperties cookieProperties;
	private final UserTrackingStorage userTrackingStorage;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String sessionCode = getSessionCode(request.getCookies());

		if (sessionCode == null) {
			return true;
		}

		if (isRoomCreationRequest(request) && isAlreadyParticipating(sessionCode)) {
			throw new SingleRoomParticipationViolationException();
		}

		return true;
	}

	private boolean isRoomCreationRequest(HttpServletRequest request) {
		return request.getMethod().equals(HttpMethod.POST.name());
	}

	private String getSessionCode(Cookie[] cookies) {
		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieProperties.getName())) {
				return cookie.getValue();
			}
		}

		return null;
	}

	private boolean isAlreadyParticipating(String sessionCode) {
		return userTrackingStorage.exists(sessionCode);
	}

}

