package site.youtogether.util.interceptor;

import static site.youtogether.util.AppConstants.*;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.exception.cookie.CookieNoExistenceException;
import site.youtogether.jwt.JwtService;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandshakeInterceptor implements HandshakeInterceptor {

	private final CookieProperties cookieProperties;
	private final JwtService jwtService;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		log.info("웹 소켓 커넥션 시작");

		ServletServerHttpRequest req = (ServletServerHttpRequest)request;
		HttpServletRequest servletRequest = req.getServletRequest();

		Stream<Cookie> cookieStream = servletRequest.getCookies() == null ? Stream.empty() : Stream.of(servletRequest.getCookies());
		Long userId = cookieStream
			.filter(cookie -> cookie.getName().equals(cookieProperties.getName()))
			.map(cookie -> jwtService.parse(cookie.getValue()))
			.findAny()
			.orElseThrow(CookieNoExistenceException::new);
		attributes.put(USER_ID, userId);

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}

}
