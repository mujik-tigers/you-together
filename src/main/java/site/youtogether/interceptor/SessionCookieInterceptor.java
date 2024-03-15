package site.youtogether.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.AppConstants;
import site.youtogether.util.RandomUtil;

@Component
@RequiredArgsConstructor
public class SessionCookieInterceptor implements HandlerInterceptor {

	private final CookieProperties cookieProperties;
	private final UserStorage userStorage;

	/**
	 * If there is no session cookie in the request, it creates a new one and saves it.
	 * If a session cookie is present, it saves the found cookie in the attributes.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieProperties.getName())) {
					request.setAttribute(AppConstants.SESSION_CODE, cookie.getValue());

					return true;
				}
			}
		}

		ResponseCookie sessionCookie = ResponseCookie.from(cookieProperties.getName(), RandomUtil.generateSessionCode())
			.domain(cookieProperties.getDomain())
			.path(cookieProperties.getPath())
			.sameSite(cookieProperties.getSameSite())
			.maxAge(cookieProperties.getExpiry())
			.httpOnly(true)
			.secure(true)
			.build();

		response.setHeader(HttpHeaders.SET_COOKIE, sessionCookie.toString());
		request.setAttribute(AppConstants.SESSION_CODE, sessionCookie.getValue());

		User user = User.builder()
			.sessionCode(sessionCookie.getValue())
			.address(request.getRemoteAddr())
			.build();

		userStorage.save(user);

		return true;
	}

}
