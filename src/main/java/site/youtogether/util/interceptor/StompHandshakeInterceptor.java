package site.youtogether.util.interceptor;

import static site.youtogether.util.AppConstants.*;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.exception.jwt.InvalidTokenException;
import site.youtogether.jwt.JwtService;
import site.youtogether.user.infrastructure.UserTrackingStorage;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandshakeInterceptor implements HandshakeInterceptor {

	private final UserTrackingStorage userTrackingStorage;
	private final JwtService jwtService;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		log.info("웹 소켓 커넥션 시작");

		String header = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
		Claims claims = jwtService.parse(header);
		if (!userTrackingStorage.exists((Long)claims.get(USER_ID))) {
			throw new InvalidTokenException();
		}
		attributes.put(USER_ID, Long.parseLong(claims.getId()));

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}

}
