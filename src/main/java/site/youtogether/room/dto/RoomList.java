package site.youtogether.room.dto;

import java.util.List;

import org.springframework.data.domain.Slice;

import lombok.Getter;
import site.youtogether.room.Room;

@Getter
public class RoomList {

	private int pageNumber;
	private int pageSize;
	private boolean hasNext;

	private List<RoomListDetail> rooms;

	public RoomList(Slice<Room> roomSlice) {
		this.pageNumber = roomSlice.getPageable().getPageNumber();
		this.pageSize = roomSlice.getPageable().getPageSize();
		this.hasNext = roomSlice.hasNext();
		this.rooms = roomSlice.getContent().stream()
			.map(RoomListDetail::new)
			.toList();
	}

}
