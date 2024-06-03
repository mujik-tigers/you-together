package site.youtogether.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import site.youtogether.util.interceptor.SessionCreateInterceptor;
import site.youtogether.util.interceptor.SingleRoomCheckInterceptor;
import site.youtogether.util.resolver.UserTrackingArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final SingleRoomCheckInterceptor singleRoomCheckInterceptor;
	private final SessionCreateInterceptor sessionCreateInterceptor;
	private final UserTrackingArgumentResolver userTrackingArgumentResolver;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000",
				"https://you-together-web.vercel.app",
				"https://localhost:3001",
				"https://you-together.site",
				"https://www.you-together.site")
			.allowedMethods("*")
			.allowCredentials(true)
			.exposedHeaders(HttpHeaders.AUTHORIZATION)
			.maxAge(3000);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sessionCreateInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/docs/index.html")
			.order(Ordered.HIGHEST_PRECEDENCE);
		registry.addInterceptor(singleRoomCheckInterceptor)
			.addPathPatterns("/rooms/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(userTrackingArgumentResolver);
	}

}
