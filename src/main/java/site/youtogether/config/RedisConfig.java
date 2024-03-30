package site.youtogether.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.application.RedisSubscriber;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	private final RedisProperties redisProperties;

	// 채팅 메시지를 관리하는 채널
	@Bean
	public ChannelTopic chatChannelTopic() {
		return new ChannelTopic("chat");
	}

	// 방에 참가한 인원 정보를 관리하는 채널
	@Bean
	public ChannelTopic participantChannelTopic() {
		return new ChannelTopic("participant");
	}

	// 채팅 메시지를 처리하는 subscriber 메서드 설정
	@Bean
	public MessageListenerAdapter chatListenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendMessage");
	}

	// 참가 인원 정보를 처리하는 subscriber 메서드 설정
	@Bean
	public MessageListenerAdapter participantInfoListenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendParticipantsInfo");
	}

	// 토픽에 발행된 메시지 처리를 위한 subscriber 메서드를 등록
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
		MessageListenerAdapter chatListenerAdapter, MessageListenerAdapter participantInfoListenerAdapter,
		ChannelTopic chatChannelTopic, ChannelTopic participantChannelTopic) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(chatListenerAdapter, chatChannelTopic);
		container.addMessageListener(participantInfoListenerAdapter, participantChannelTopic);
		return container;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(),
			redisProperties.getPort());
		redisStandaloneConfiguration.setPassword(redisProperties.getPassword());

		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

}
