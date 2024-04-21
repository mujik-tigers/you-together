package site.youtogether.util.resolver;

import static site.youtogether.util.AppConstants.*;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserTrackingArgumentResolver implements HandlerMethodArgumentResolver {

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
		return (Long)request.getAttribute(USER_ID);
	}

}
