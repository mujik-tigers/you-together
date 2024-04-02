package site.youtogether.interceptor;

import static site.youtogether.util.AppConstants.*;

import java.util.Map;
import java.util.Objects;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandshakeInterceptor implements HandshakeInterceptor {

	/**
	 * @param attributes the attributes from the HTTP handshake to associate with the WebSocket
	 * client sends the received cookies along to attempt a handshake, extracting the session code from the cookies.
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {
		String[] cookies = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.COOKIE)).get(0).split("; ");
		String sessionCode = Stream.of(cookies)
			.filter(cookie -> cookie.startsWith(SESSION_COOKIE_NAME))
			.map(cookie -> cookie.substring(cookie.indexOf("=") + 1))
			.findAny()
			.orElseThrow(SessionCookieNoExistenceException::new);

		attributes.put(SESSION_CODE, sessionCode);

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
	}

}
