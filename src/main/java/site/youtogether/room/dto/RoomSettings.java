package site.youtogether.room.dto;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RoomSettings {

	@NotBlank(message = "공백이 아닌 문자를 1개 이상 입력해 주세요")
	@Size(min = 1, max = 30, message = "제목은 {min}자 이상 {max}자 이하로 입력해 주세요")
	private String title;

	@Range(min = 2, max = 10, message = "정원은 {min}명 이상 {max}명 이하로 입력해 주세요")
	private int capacity;

	@Pattern(regexp = "^[0-9a-zA-Z]{1,30}$", message = "비밀번호는 1자 이상 30자 이하의 영문 또는 숫자로 입력해 주세요")
	private String password;

	@Builder
	public RoomSettings(String title, int capacity, String password) {
		this.title = title;
		this.capacity = capacity;
		this.password = password;
	}

}
