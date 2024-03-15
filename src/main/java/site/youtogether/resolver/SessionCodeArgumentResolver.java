package site.youtogether.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import site.youtogether.util.AppConstants;

@Component
public class SessionCodeArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasAddressAnnotation = parameter.hasParameterAnnotation(SessionCode.class);
		boolean isStringType = String.class.isAssignableFrom(parameter.getParameterType());

		return hasAddressAnnotation && isStringType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

		assert request != null;
		return request.getAttribute(AppConstants.SESSION_CODE);
	}

}
