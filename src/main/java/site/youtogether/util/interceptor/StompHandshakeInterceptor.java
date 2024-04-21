package site.youtogether.util.interceptor;

import static site.youtogether.util.AppConstants.*;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StompHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		log.info("웹 소켓 커넥션 시작");

		ServletServerHttpRequest req = (ServletServerHttpRequest)request;
		HttpServletRequest servletRequest = req.getServletRequest();
		Long userId = (Long)servletRequest.getAttribute(USER_ID);
		attributes.put(USER_ID, userId);

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}

}
