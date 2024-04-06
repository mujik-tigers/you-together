package site.youtogether.room.infrastructure;

import com.redis.om.spring.repository.RedisDocumentRepository;

import site.youtogether.room.Room;

public interface RoomStorage extends RedisDocumentRepository<Room, String>, RoomCustomRepository {

}
