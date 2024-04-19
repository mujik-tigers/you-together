package site.youtogether.util.interceptor;

import static site.youtogether.util.AppConstants.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.jwt.JwtService;
import site.youtogether.user.infrastructure.UserTrackingStorage;

@Component
@RequiredArgsConstructor
public class SingleRoomCheckInterceptor implements HandlerInterceptor {

	private final JwtService jwtService;
	private final UserTrackingStorage userTrackingStorage;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (CorsUtils.isPreFlightRequest(request))
			return true;

		String requestMethod = request.getMethod();
		if (!HttpMethod.POST.matches(requestMethod)) {    // POST가 아니면 검증 통과
			return true;
		}

		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null) {
			return true;
		}

		Claims claims = jwtService.parse(authorizationHeader);
		if (userTrackingStorage.exists((Long)claims.get(USER_ID))) {
			throw new SingleRoomParticipationViolationException();
		}

		return true;
	}

}
