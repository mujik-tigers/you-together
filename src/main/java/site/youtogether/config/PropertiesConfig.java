package site.youtogether.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import site.youtogether.config.property.CookieProperties;

@Configuration
@EnableConfigurationProperties(value = {
	CookieProperties.class
})
public class PropertiesConfig {

}
