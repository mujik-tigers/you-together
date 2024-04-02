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

	@Bean
	public ChannelTopic chatChannelTopic() {
		return new ChannelTopic("chat");
	}

	@Bean
	public ChannelTopic participantChannelTopic() {
		return new ChannelTopic("participant");
	}

	@Bean
	public MessageListenerAdapter chatMessageListenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendChatMessage");
	}

	@Bean
	public MessageListenerAdapter participantsInfoListenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendParticipantsInfo");
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
		MessageListenerAdapter chatMessageListenerAdapter, MessageListenerAdapter participantsInfoListenerAdapter,
		ChannelTopic chatChannelTopic, ChannelTopic participantChannelTopic) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(chatMessageListenerAdapter, chatChannelTopic);
		container.addMessageListener(participantsInfoListenerAdapter, participantChannelTopic);

		return container;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
		redisStandaloneConfiguration.setPassword(redisProperties.getPassword());

		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

}
