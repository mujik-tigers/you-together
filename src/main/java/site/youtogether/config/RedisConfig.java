package site.youtogether.config;

import java.util.List;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;

import lombok.RequiredArgsConstructor;
import site.youtogether.message.ChatHistory;

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
	public DefaultRedisScript<List> batchRemoveScript() {
		DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/batch-removal-operation.lua")));
		redisScript.setResultType(List.class);
		return redisScript;
	}

	@Bean
	public DefaultRedisScript<Boolean> updateUniqueNicknameScript() {
		DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/update-unique-nickname.lua")));
		redisScript.setResultType(Boolean.class);
		return redisScript;
	}

	@Bean
	public RedisTemplate<String, ChatHistory> redisTemplate() {
		RedisTemplate<String, ChatHistory> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatHistory.class));
		return redisTemplate;
	}

}
