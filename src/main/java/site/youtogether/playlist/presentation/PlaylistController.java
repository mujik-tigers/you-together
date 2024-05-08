package site.youtogether.playlist.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.playlist.application.PlaylistService;
import site.youtogether.playlist.dto.NextVideo;
import site.youtogether.playlist.dto.PlaylistAddForm;
import site.youtogether.playlist.dto.VideoDeletion;
import site.youtogether.playlist.dto.VideoOrder;
import site.youtogether.util.api.ApiResponse;
import site.youtogether.util.api.ResponseResult;
import site.youtogether.util.resolver.UserTracking;

@RestController
@RequiredArgsConstructor
public class PlaylistController {

	private final PlaylistService playlistService;

	@PostMapping("/playlists")
	public ResponseEntity<ApiResponse<Void>> addVideo(@UserTracking Long userId, @RequestBody PlaylistAddForm form) {
		playlistService.addVideo(userId, form);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.created(ResponseResult.PLAYLIST_ADD_SUCCESS, null));
	}

	@PostMapping("/playlists/next")
	public ResponseEntity<ApiResponse<Void>> playNextVideo(@UserTracking Long userId, @RequestBody NextVideo nextVideo) {
		playlistService.playNextVideo(userId, nextVideo.getVideoNumber());

		return ResponseEntity.ok(
			ApiResponse.ok(ResponseResult.PLAY_NEXT_VIDEO_SUCCESS, null));
	}

	@PatchMapping("/playlists")
	public ResponseEntity<ApiResponse<Void>> reorderVideo(@UserTracking Long userId, @RequestBody VideoOrder videoOrder) {
		playlistService.reorderVideo(userId, videoOrder);

		return ResponseEntity.ok(
			ApiResponse.ok(ResponseResult.PLAYLIST_REORDER_SUCCESS, null));
	}

	@DeleteMapping("/playlists")
	public ResponseEntity<ApiResponse<Void>> deleteVideo(@UserTracking Long userId, @RequestBody VideoDeletion videoDeletion) {
		playlistService.deleteVideo(userId, videoDeletion.getVideoIndex(), videoDeletion.getVideoNumber());

		return ResponseEntity.ok(
			ApiResponse.ok(ResponseResult.PLAYLIST_DELETE_SUCCESS, null));
	}

}
