package site.youtogether.util.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.youtogether.exception.jwt.InvalidTokenException;
import site.youtogether.jwt.JwtService;
import site.youtogether.user.infrastructure.UserTrackingStorage;

@Component
@RequiredArgsConstructor
public class UserTrackingArgumentResolver implements HandlerMethodArgumentResolver {

	private final UserTrackingStorage userTrackingStorage;
	private final JwtService jwtService;

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
		Long userId = jwtService.parse(request.getHeader(HttpHeaders.AUTHORIZATION));

		if (!userTrackingStorage.exists(userId)) {
			throw new InvalidTokenException();
		}

		return userId;
	}

}
