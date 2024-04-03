package site.youtogether.room.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomList {

	private int pageNumber;
	private int pageSize;
	private int totalData;
	private int totalPage;
	private boolean hasPrevious;
	private boolean hasNext;

	private List<RoomDetail> rooms;

	@Builder
	public RoomList(int pageNumber, int pageSize, int totalData, int totalPage, boolean hasPrevious, boolean hasNext, List<RoomDetail> rooms) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.totalData = totalData;
		this.totalPage = totalPage;
		this.hasPrevious = hasPrevious;
		this.hasNext = hasNext;
		this.rooms = rooms;
	}

}
