package site.youtogether.message;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompSession;

import site.youtogether.StompSupport;

public class ChattingTest extends StompSupport {

	@Test
	@DisplayName("채팅을 보낸 사람은 자신이 보낸 메시지를 받아서 볼 수 있다.")
	void senderReceiveMessage() throws Exception {
		// given
		String roomId = "dfahlljd";
		MessageFrameHandler<ChatMessage> handler = new MessageFrameHandler<>(ChatMessage.class);
		ChatMessage sendMessage = new ChatMessage(roomId, "황똥땡", "바~보", MessageType.TALK);
		this.stompSessions.get(0).subscribe("/sub/chat/room/" + roomId, handler);

		// when
		this.stompSessions.get(0).send("/pub/chat/message", sendMessage);

		// then
		ChatMessage receiveMessage = handler.getCompletableFuture().get(3, TimeUnit.SECONDS);
		assertThat(receiveMessage).usingRecursiveComparison().isEqualTo(sendMessage);
	}

	@Test
	@DisplayName("같은 채팅방에 존재하는 사람은, 다른 사람이 보낸 메시지를 받아서 볼 수 있다.")
	void receiverReceiveMessage() throws Exception {
		// given
		String roomId = "adsflksd";
		MessageFrameHandler<ChatMessage> handler = new MessageFrameHandler<>(ChatMessage.class);
		StompSession receiver = this.stompSessions.get(1);
		receiver.subscribe("/sub/chat/room/" + roomId, handler);

		StompSession sender = this.stompSessions.get(0);
		ChatMessage sendMessage = new ChatMessage(roomId, "황똥땡", "바~보", MessageType.TALK);

		// when
		sender.send("/pub/chat/message", sendMessage);

		// then
		ChatMessage receiveMessage = handler.getCompletableFuture().get(3, TimeUnit.SECONDS);
		assertThat(receiveMessage).usingRecursiveComparison().isEqualTo(sendMessage);
	}

}
