package site.youtogether.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.application.RedisSubscriber;

@Configuration
@EnableRedisDocumentRepositories(basePackages = "site.youtogether.*")
@RequiredArgsConstructor
public class RedisConfig {

	private final RedisProperties redisProperties;

	// 채팅 메시지를 관리하는 채널
	@Bean
	public ChannelTopic chatChannelTopic() {
		return new ChannelTopic("chat");
	}

	// 채팅 메시지를 처리할 subscriber 메서드 설정
	@Bean
	public MessageListenerAdapter chatListenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendChat");
	}

	// 방에 참가한 인원 정보를 관리하는 채널
	@Bean
	public ChannelTopic participantChannelTopic() {
		return new ChannelTopic("participant");
	}

	// 참가 인원 정보를 처리할 subscriber 메서드 설정
	@Bean
	public MessageListenerAdapter participantsInfoListenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendParticipantsInfo");
	}

	// 채널에 발행된 메시지 처리를 위한 subscriber 메서드를 컨테이너에 등록
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
		MessageListenerAdapter chatListenerAdapter, MessageListenerAdapter participantsInfoListenerAdapter,
		ChannelTopic chatChannelTopic, ChannelTopic participantChannelTopic) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(chatListenerAdapter, chatChannelTopic);
		container.addMessageListener(participantsInfoListenerAdapter, participantChannelTopic);

		return container;
	}

	@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(),
			redisProperties.getPort());
		redisStandaloneConfiguration.setPassword(redisProperties.getPassword());

		return new JedisConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<String, Long> redisTemplate() {
		final RedisTemplate<String, Long> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
		template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
		return template;
	}

}
