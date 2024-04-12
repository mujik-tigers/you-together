package site.youtogether.util.interceptor;

import static site.youtogether.util.AppConstants.*;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.exception.cookie.CookieInvalidException;
import site.youtogether.exception.cookie.CookieNoExistenceException;
import site.youtogether.user.infrastructure.UserTrackingStorage;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandshakeInterceptor implements HandshakeInterceptor {

	private final CookieProperties cookieProperties;
	private final UserTrackingStorage userTrackingStorage;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		log.info("웹 소켓 커넥션 시작");

		String[] cookies = request.getHeaders().get(HttpHeaders.COOKIE).get(0).split("; ");
		String cookieValue = Stream.of(cookies)
			.filter(cookie -> cookie.startsWith(cookieProperties.getName()))
			.map(cookie -> cookie.substring(cookie.indexOf("=") + 1))
			.findAny()
			.orElseThrow(CookieNoExistenceException::new);

		Long userId = userTrackingStorage.findByCookieValue(cookieValue)
			.orElseThrow(CookieInvalidException::new);
		attributes.put(USER_ID, userId);

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}

}
