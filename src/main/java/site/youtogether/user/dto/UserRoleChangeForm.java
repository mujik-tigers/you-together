package site.youtogether.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.user.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserRoleChangeForm {

	@NotBlank
	private String roomCode;

	@NotNull
	private Long changedUserId;
	private Role changeUserRole;

}
