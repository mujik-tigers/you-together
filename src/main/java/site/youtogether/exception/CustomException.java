package site.youtogether.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus status;

	public CustomException(ErrorType errorType) {
		super(errorType.getMessage());
		this.status = errorType.getStatus();
	}

}
