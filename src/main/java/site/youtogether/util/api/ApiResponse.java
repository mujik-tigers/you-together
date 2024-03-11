package site.youtogether.util.api;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

	private final int code;
	private final String status;
	private final String result;
	private final T data;

	public ApiResponse(HttpStatus status, ResponseResult result, T data) {
		this.code = status.value();
		this.status = status.getReasonPhrase();
		this.result = result.getDescription();
		this.data = data;
	}

	public static <T> ApiResponse<T> of(HttpStatus status, ResponseResult result, T data) {
		return new ApiResponse<>(status, result, data);
	}

	public static <T> ApiResponse<T> ok(ResponseResult result, T data) {
		return new ApiResponse<>(HttpStatus.OK, result, data);
	}

	public static <T> ApiResponse<T> created(ResponseResult result, T data) {
		return new ApiResponse<>(HttpStatus.CREATED, result, data);
	}

}
