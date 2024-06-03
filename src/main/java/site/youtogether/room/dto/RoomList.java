package site.youtogether.room.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class RoomList {

	private int pageNumber;
	private int pageSize;
	private boolean hasNext;

	private List<RoomListDetail> rooms;

	public RoomList(int pageNumber, int pageSize, boolean hasNext, List<RoomListDetail> rooms) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.hasNext = hasNext;
		this.rooms = rooms;
	}

}
