package site.youtogether.room.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PasswordInput {

	@Pattern(regexp = "^[0-9a-zA-Z]{1,30}$", message = "비밀번호는 1자 이상 30자 이하의 영문 또는 숫자로 입력해 주세요")
	private String passwordInput;

}
