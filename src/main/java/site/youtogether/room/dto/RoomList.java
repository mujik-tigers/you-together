package site.youtogether.room.dto;

import java.util.List;

import org.springframework.data.domain.Slice;

import lombok.Getter;

@Getter
public class RoomList {

	private final boolean last;
	private final int pageNumber;
	private final List<RoomInfo> rooms;

	public RoomList(Slice<RoomInfo> roomInfoSlice) {
		this.last = roomInfoSlice.isLast();
		this.pageNumber = roomInfoSlice.getNumber();
		this.rooms = roomInfoSlice.getContent();
	}

}
