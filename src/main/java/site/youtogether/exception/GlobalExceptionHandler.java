package site.youtogether.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import site.youtogether.util.api.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiResponse<Object>> handleBindException(BindException exception) {
		return ResponseEntity.badRequest().body(
			ApiResponse.of(HttpStatus.BAD_REQUEST,
				exception.getBindingResult().getFieldErrors().stream()
					.collect(Collectors.groupingBy(FieldError::getField))
					.entrySet().stream()
					.map(error -> {
						Map<String, java.lang.Object> fieldError = new HashMap<>();
						fieldError.put("field", error.getKey());
						fieldError.put("message", error.getValue().stream()
							.map(DefaultMessageSourceResolvable::getDefaultMessage)
							.collect(Collectors.joining(", ")));
						return fieldError;
					})
			)
		);
	}

}
