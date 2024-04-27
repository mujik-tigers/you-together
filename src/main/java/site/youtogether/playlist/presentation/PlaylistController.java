package site.youtogether.playlist.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.playlist.application.PlaylistService;
import site.youtogether.playlist.dto.PlaylistAddForm;
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

		return ResponseEntity.ok(
			ApiResponse.ok(ResponseResult.PLAYLIST_ADD_SUCCESS, null));
	}

}
