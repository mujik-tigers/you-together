package site.youtogether;

import static site.youtogether.util.AppConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class StompSupport {

	private static final int SESSION_COUNT = 2;

	protected List<StompSession> stompSessions = new ArrayList<>();

	@LocalServerPort
	private int port;

	private final String url;
	private final WebSocketStompClient websocketClient;

	public StompSupport() {
		this.url = "ws://localhost:";

		this.websocketClient = new WebSocketStompClient(new SockJsClient(createTransport()));
		this.websocketClient.setMessageConverter(new MappingJackson2MessageConverter());
	}

	@BeforeEach
	public void connect() throws ExecutionException, InterruptedException, TimeoutException {
		for (int i = 0; i < SESSION_COUNT; i++) {
			this.stompSessions.add(this.websocketClient
				.connectAsync(url + port + STOMP_ENDPOINT, new StompSessionHandlerAdapter() {
				})
				.get(3, TimeUnit.SECONDS));
		}
	}

	@AfterEach
	public void disconnect() {
		this.stompSessions.stream()
			.filter(StompSession::isConnected)
			.forEach(StompSession::disconnect);
	}

	private List<Transport> createTransport() {
		List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));

		return transports;
	}

}
