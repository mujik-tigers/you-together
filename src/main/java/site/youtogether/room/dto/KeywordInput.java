package site.youtogether.room.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class KeywordInput {

	@Pattern(regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ ]+$", message = "특수문자를 제거해주세요")
	private String keyword;

}
