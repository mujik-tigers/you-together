package site.youtogether.util.interceptor;

import static site.youtogether.util.AppConstants.*;

import java.time.Duration;
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
import lombok.extern.slf4j.Slf4j;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.jwt.JwtService;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UniqueNicknameStorage;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCreateInterceptor implements HandlerInterceptor {

	private final CookieProperties cookieProperties;
	private final UserStorage userStorage;
	private final JwtService jwtService;
	private final UniqueNicknameStorage uniqueNicknameStorage;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (CorsUtils.isPreFlightRequest(request)) {
			return true;
		}
		Stream<Cookie> cookieStream = request.getCookies() == null ? Stream.empty() : Stream.of(request.getCookies());
		Long userId = cookieStream
			.filter(cookie -> cookie.getName().equals(cookieProperties.getName()))
			.map(cookie -> jwtService.parse(cookie.getValue()))
			.filter(uid -> userStorage.existsById(uid))
			.findAny()
			.orElseGet(() -> generateSession(request, response));

		log.info("--USER ID {} 세션 생성 인터셉터 통과함--", userId);
		request.setAttribute(USER_ID, userId);
		return true;
	}

	private Long generateSession(HttpServletRequest request, HttpServletResponse response) {
		Long userId = RandomUtil.generateUserId();
		String newToken = jwtService.issue(userId, Duration.ofSeconds(cookieProperties.getExpiry()));
		request.setAttribute(USER_ID, userId);
		generateCookie(response, newToken);

		String randomNickname = RandomUtil.generateUserNickname();
		while (uniqueNicknameStorage.exist(randomNickname)) {
			randomNickname = RandomUtil.generateUserNickname();
		}

		User user = User.builder()
			.id(userId)
			.nickname(randomNickname)
			.currentRoomCode(null)
			.activate(true)
			.build();
		userStorage.save(user);
		uniqueNicknameStorage.save(randomNickname);

		return userId;
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
