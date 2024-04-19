package site.youtogether.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableRedisDocumentRepositories(basePackages = "site.youtogether.*")
@RequiredArgsConstructor
public class RedisConfig {

	private final RedisProperties redisProperties;

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
