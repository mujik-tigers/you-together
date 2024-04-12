package site.youtogether.exception;

import static site.youtogether.util.AppConstants.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.youtogether.config.property.CookieProperties;
import site.youtogether.exception.room.PasswordNotMatchException;
import site.youtogether.util.RandomUtil;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final CookieProperties cookieProperties;

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiResponse<Object>> handleBindException(BindException exception) {
		return ResponseEntity.badRequest()
			.body(ApiResponse.of(HttpStatus.BAD_REQUEST, ResponseResult.EXCEPTION_OCCURRED,
					exception.getBindingResult().getFieldErrors().stream()
						.collect(Collectors.groupingBy(FieldError::getField))
						.entrySet().stream()
						.map(error -> {
							Map<String, Object> fieldError = new LinkedHashMap<>();
							fieldError.put("type", error.getKey());
							fieldError.put("message", error.getValue().stream()
								.map(DefaultMessageSourceResolvable::getDefaultMessage)
								.collect(Collectors.joining(", ")));
							return fieldError;
						})
				)
			);
	}

	@ExceptionHandler(PasswordNotMatchException.class)
	public ResponseEntity<ApiResponse<Object>> handlePasswordNotMatchException(PasswordNotMatchException exception, HttpServletResponse response) {
		invalidateSessionCookie(response);
		
		Map<String, String> error = new LinkedHashMap<>(2);
		error.put("type", exception.getClass().getSimpleName());
		error.put("message", exception.getMessage());

		return ResponseEntity.status(exception.getStatus())
			.body(ApiResponse.of(exception.getStatus(), ResponseResult.EXCEPTION_OCCURRED, List.of(error)));
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException customException) {
		Map<String, String> error = new LinkedHashMap<>(2);
		error.put("type", customException.getClass().getSimpleName());
		error.put("message", customException.getMessage());

		return ResponseEntity.status(customException.getStatus())
			.body(ApiResponse.of(customException.getStatus(), ResponseResult.EXCEPTION_OCCURRED, List.of(error)));
	}

	private void invalidateSessionCookie(HttpServletResponse response) {
		ResponseCookie sessionCookie = ResponseCookie.from(cookieProperties.getName(), RandomUtil.generateRandomCode(COOKIE_VALUE_LENGTH))
			.domain(cookieProperties.getDomain())
			.path(cookieProperties.getPath())
			.sameSite(cookieProperties.getSameSite())
			.maxAge(0)
			.httpOnly(true)
			.secure(true)
			.build();

		response.setHeader(HttpHeaders.SET_COOKIE, sessionCookie.toString());
	}

}
