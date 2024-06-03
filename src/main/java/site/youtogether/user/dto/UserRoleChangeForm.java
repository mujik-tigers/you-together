package site.youtogether.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.youtogether.user.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserRoleChangeForm {

	@NotNull
	private Long targetUserId;
	private Role newUserRole;

}
