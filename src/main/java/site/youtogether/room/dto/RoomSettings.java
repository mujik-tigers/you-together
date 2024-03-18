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

	@NotBlank
	@Size(min = 1, max = 30)
	private String title;

	@Range(min = 2, max = 10)
	private int capacity;

	@Pattern(regexp = "^[0-9a-zA-Z]{5,10}$")
	private String password;

	@Builder
	public RoomSettings(String title, int capacity, String password) {
		this.title = title;
		this.capacity = capacity;
		this.password = password;
	}

}
