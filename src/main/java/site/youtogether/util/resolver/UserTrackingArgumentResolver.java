package site.youtogether.util.resolver;

import java.util.stream.Stream;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.exception.cookie.CookieInvalidException;
import site.youtogether.exception.cookie.CookieNoExistenceException;
import site.youtogether.user.infrastructure.UserTrackingStorage;

@Component
@RequiredArgsConstructor
public class UserTrackingArgumentResolver implements HandlerMethodArgumentResolver {

	private final CookieProperties cookieProperties;
	private final UserTrackingStorage userTrackingStorage;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasUserTrackingAnnotation = parameter.hasParameterAnnotation(UserTracking.class);
		boolean hasLongType = Long.class.isAssignableFrom(parameter.getParameterType());

		return hasUserTrackingAnnotation && hasLongType;
	}

	@Override
	public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

		assert request != null;
		Stream<Cookie> cookieStream = request.getCookies() == null ? Stream.empty() : Stream.of(request.getCookies());
		String cookieValue = cookieStream
			.filter(cookie -> cookie.getName().equals(cookieProperties.getName()))
			.map(cookie -> cookie.getValue())
			.findAny()
			.orElseThrow(CookieNoExistenceException::new);

		return userTrackingStorage.findByCookieValue(cookieValue)
			.orElseThrow(CookieInvalidException::new);
	}

}
