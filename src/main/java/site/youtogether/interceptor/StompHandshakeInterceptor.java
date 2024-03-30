package site.youtogether.interceptor;

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
import site.youtogether.exception.cookie.SessionCookieNoExistenceException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandshakeInterceptor implements HandshakeInterceptor {

	private final UserStorage userStorage;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		String[] cookies = request.getHeaders().get(HttpHeaders.COOKIE).get(0).split("; ");
		String sessionCode = Stream.of(cookies)
			.filter(cookie -> cookie.startsWith(SESSION_COOKIE_NAME))
			.map(cookie -> cookie.substring(cookie.indexOf("=") + 1))
			.findAny()
			.orElseThrow(SessionCookieNoExistenceException::new);

		String userNickname = userStorage.findById(sessionCode)
			.map(User::getNickname)
			.orElseThrow(UserNoExistenceException::new);

		attributes.put(STOMP_SESSION_NICKNAME, userNickname);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
	}

}
