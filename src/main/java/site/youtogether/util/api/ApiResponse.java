package site.youtogether.util.api;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

	private final int code;
	private final String status;
	private final T data;

	public ApiResponse(HttpStatus status, T data) {
		this.code = status.value();
		this.status = status.getReasonPhrase();
		this.data = data;
	}

	public static <T> ApiResponse<T> of(HttpStatus status, T data) {
		return new ApiResponse<>(status, data);
	}

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(HttpStatus.OK, data);
	}

	public static <T> ApiResponse<T> created(T data) {
		return new ApiResponse<>(HttpStatus.CREATED, data);
	}

}
