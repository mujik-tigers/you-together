package site.youtogether.room.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCreateForm {

	private String name;
	private String password;
	private int totalCapacity;

}
