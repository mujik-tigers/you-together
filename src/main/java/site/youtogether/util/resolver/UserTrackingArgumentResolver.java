package site.youtogether.util.resolver;

import static site.youtogether.util.AppConstants.*;

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
import site.youtogether.exception.cookie.CookieNoExistenceException;
import site.youtogether.user.infrastructure.UserTrackingStorage;

@Component
@RequiredArgsConstructor
public class UserTrackingArgumentResolver implements HandlerMethodArgumentResolver {

	private final UserTrackingStorage userTrackingStorage;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasUserTrackingAnnotation = parameter.hasParameterAnnotation(UserTracking.class);
		boolean hasStringType = String.class.isAssignableFrom(parameter.getParameterType());

		return hasUserTrackingAnnotation && hasStringType;
	}

	@Override
	public String resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

		assert request != null;
		Stream<Cookie> cookieStream = request.getCookies() == null ? Stream.empty() : Stream.of(request.getCookies());
		String cookieValue = cookieStream
			.filter(cookie -> cookie.getName().equals(SESSION_COOKIE_NAME))
			.map(cookie -> cookie.getValue())
			.findAny()
			.orElseThrow(CookieNoExistenceException::new);

		return userTrackingStorage.findByCookieValue(cookieValue);
	}

}
