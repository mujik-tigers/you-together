package site.youtogether.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import site.youtogether.interceptor.SessionCookieInterceptor;
import site.youtogether.resolver.SessionCodeArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final SessionCookieInterceptor sessionCookieInterceptor;
	private final SessionCodeArgumentResolver sessionCodeArgumentResolver;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sessionCookieInterceptor)
			.addPathPatterns("/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(sessionCodeArgumentResolver);
	}

}
