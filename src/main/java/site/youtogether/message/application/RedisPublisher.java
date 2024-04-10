package site.youtogether.message.application;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.ChatMessage;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

	private final ChannelTopic chatChannelTopic;
	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public void publishMessage(ChatMessage chatMessage) {
		try {
			redisTemplate.convertAndSend(chatChannelTopic.getTopic(), objectMapper.writeValueAsString(chatMessage));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
