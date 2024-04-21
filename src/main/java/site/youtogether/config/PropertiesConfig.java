package site.youtogether.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import site.youtogether.config.property.CookieProperties;
import site.youtogether.config.property.JwtProperties;

@Configuration
@EnableConfigurationProperties(value = {
	CookieProperties.class,
	JwtProperties.class
})
public class PropertiesConfig {

}
