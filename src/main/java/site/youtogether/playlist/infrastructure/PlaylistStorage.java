package site.youtogether.playlist.infrastructure;

import com.redis.om.spring.repository.RedisDocumentRepository;

import site.youtogether.playlist.Playlist;

public interface PlaylistStorage extends RedisDocumentRepository<Playlist, String> {
}
