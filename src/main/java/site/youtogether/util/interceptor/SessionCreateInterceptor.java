package site.youtogether.util.interceptor;

import static site.youtogether.util.AppConstants.*;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.jwt.JwtService;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

@Component
@RequiredArgsConstructor
public class SessionCreateInterceptor implements HandlerInterceptor {

	private final CookieProperties cookieProperties;
	private final UserStorage userStorage;
	private final JwtService jwtService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (CorsUtils.isPreFlightRequest(request)) {
			return true;
		}

		Stream<Cookie> cookieStream = request.getCookies() == null ? Stream.empty() : Stream.of(request.getCookies());
		Optional<String> token = cookieStream
			.filter(cookie -> cookie.getName().equals(cookieProperties.getName()))
			.filter(cookie -> jwtService.isValidToken(cookie.getValue()))
			.map(cookie -> cookie.getValue())
			.findAny();

		if (token.isEmpty()) {
			Long userId = RandomUtil.generateUserId();
			String newToken = jwtService.issue(userId, Duration.ofSeconds(cookieProperties.getExpiry()));
			request.setAttribute(USER_ID, userId);
			generateCookie(response, newToken);

			User user = User.builder()
				.id(userId)
				.nickname(RandomUtil.generateUserNickname())
				.role(null)
				.currentRoomCode(null)
				.build();
			userStorage.save(user);

			return true;
		}

		Long userId = jwtService.parse(token.get());
		request.setAttribute(USER_ID, userId);

		return true;
	}

	private void generateCookie(HttpServletResponse response, String token) {
		ResponseCookie cookie = ResponseCookie.from(cookieProperties.getName(), token)
			.domain(cookieProperties.getDomain())
			.path(cookieProperties.getPath())
			.sameSite(cookieProperties.getSameSite())
			.maxAge(cookieProperties.getExpiry())
			.httpOnly(true)
			.secure(true)
			.build();

		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

}
