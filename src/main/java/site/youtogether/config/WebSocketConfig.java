package site.youtogether.config;

import static site.youtogether.util.AppConstants.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;
import site.youtogether.util.interceptor.StompHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final StompHandshakeInterceptor stompHandshakeInterceptor;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/sub");
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(STOMP_ENDPOINT)
			.setAllowedOriginPatterns("http://localhost:3000", "https://you-together-web.vercel.app", "https://localhost:3001")
			.addInterceptors(stompHandshakeInterceptor)
			.withSockJS();
	}

}
