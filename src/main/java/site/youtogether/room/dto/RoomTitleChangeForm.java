package site.youtogether.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RoomTitleChangeForm {

	@NotNull
	private final String roomCode;

	@NotBlank(message = "공백이 아닌 문자를 1개 이상 입력해 주세요")
	@Size(min = 1, max = 30, message = "제목은 {min}자 이상 {max}자 이하로 입력해 주세요")
	private final String newTitle;

}
