package site.youtogether.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ConfigurationProperties("cookie")
@RequiredArgsConstructor
@Getter
public class CookieProperties {

	private final String name;
	private final String domain;
	private final String path;
	private final String sameSite;
	private final int expiry;

}
