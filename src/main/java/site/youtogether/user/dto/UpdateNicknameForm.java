package site.youtogether.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateNicknameForm {

	@NotBlank(message = "공백이 아닌 문자를 1개 이상 입력해 주세요")
	private String updateName;

}
