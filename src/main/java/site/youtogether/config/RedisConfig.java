package site.youtogether.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

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
	public DefaultRedisScript<Void> batchRemoveScript() {
		DefaultRedisScript<Void> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/batch-removal-operation.lua")));
		redisScript.setResultType(Void.class);
		return redisScript;
	}

}
