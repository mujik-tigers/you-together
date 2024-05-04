package site.youtogether.playlist.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.youtogether.RestDocsSupport;
import site.youtogether.exception.playlist.InvalidVideoOrderException;
import site.youtogether.exception.playlist.PlaylistEmptyException;
import site.youtogether.exception.user.VideoEditDeniedException;
import site.youtogether.playlist.dto.PlaylistAddForm;
import site.youtogether.util.api.ResponseResult;

class PlaylistControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("플레이리스트 추가 성공")
	void addPlaylist() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);
		PlaylistAddForm form = new PlaylistAddForm("TXI1npEFNss", "What a beautiful song by Sia ❤️ #snowman", "Raymond Salgado",
			"https://i.ytimg.com/vi/TXI1npEFNss/hqdefault.jpg", "PT1M21S");

		given(jwtService.isValidToken(eq(token)))
			.willReturn(true);
		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		doNothing()
			.when(playlistService).addVideo(eq(1L), any(PlaylistAddForm.class));

		// when // then
		mockMvc.perform(post("/playlists")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.PLAYLIST_ADD_SUCCESS.getDescription()))
			.andDo(document("playlist-add-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("videoId").type(JsonFieldType.STRING).description("영상 ID"),
					fieldWithPath("videoTitle").type(JsonFieldType.STRING).description("영상 제목"),
					fieldWithPath("channelTitle").type(JsonFieldType.STRING).description("채널 이름"),
					fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("영상 썸네일 URL"),
					fieldWithPath("duration").type(JsonFieldType.STRING).description("영상 길이")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)
			));
	}

	@Test
	@DisplayName("플레이리스트 추가 실패: EDITOR보다 낮은 역할입니다")
	void addPlaylistFail() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);
		PlaylistAddForm form = new PlaylistAddForm("TXI1npEFNss", "What a beautiful song by Sia ❤️ #snowman", "Raymond Salgado",
			"https://i.ytimg.com/vi/TXI1npEFNss/hqdefault.jpg", "PT1M21S");

		given(jwtService.isValidToken(eq(token)))
			.willReturn(true);
		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		doThrow(new VideoEditDeniedException())
			.when(playlistService).addVideo(eq(1L), any(PlaylistAddForm.class));

		// when // then
		mockMvc.perform(post("/playlists")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(document("playlist-add-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("videoId").type(JsonFieldType.STRING).description("영상 ID"),
					fieldWithPath("videoTitle").type(JsonFieldType.STRING).description("영상 제목"),
					fieldWithPath("channelTitle").type(JsonFieldType.STRING).description("채널 이름"),
					fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("영상 썸네일 URL"),
					fieldWithPath("duration").type(JsonFieldType.STRING).description("영상 길이")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터"),
					fieldWithPath("data[].type").type(JsonFieldType.STRING).description("오류 타입"),
					fieldWithPath("data[].message").type(JsonFieldType.STRING).description("오류 메시지")
				)
			));
	}

	@Test
	@DisplayName("다음 영상 재생 성공")
	void playNextVideo() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		given(jwtService.isValidToken(eq(token)))
			.willReturn(true);
		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		doNothing()
			.when(playlistService).playNextVideo(eq(1L));

		// when // then
		mockMvc.perform(post("/playlists/next")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.PLAY_NEXT_VIDEO_SUCCESS.getDescription()))
			.andDo(document("play-next-video-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)
			));
	}

	@Test
	@DisplayName("다음 영상 재생 실패: EDITOR보다 낮은 역할입니다")
	void playNextVideoFailNotEditable() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		given(jwtService.isValidToken(eq(token)))
			.willReturn(true);
		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		doThrow(new VideoEditDeniedException())
			.when(playlistService).playNextVideo(eq(1L));

		// when // then
		mockMvc.perform(post("/playlists/next")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(document("play-next-video-fail-not-editable",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터"),
					fieldWithPath("data[].type").type(JsonFieldType.STRING).description("오류 타입"),
					fieldWithPath("data[].message").type(JsonFieldType.STRING).description("오류 메시지")
				)
			));
	}

	@Test
	@DisplayName("다음 영상 재생 실패: 플레이리스트가 비었습니다")
	void playNextVideoFailPlaylistEmpty() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		given(jwtService.isValidToken(eq(token)))
			.willReturn(true);
		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		doThrow(new PlaylistEmptyException())
			.when(playlistService).playNextVideo(eq(1L));

		// when // then
		mockMvc.perform(post("/playlists/next")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(document("play-next-video-fail-playlist-empty",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터"),
					fieldWithPath("data[].type").type(JsonFieldType.STRING).description("오류 타입"),
					fieldWithPath("data[].message").type(JsonFieldType.STRING).description("오류 메시지")
				)
			));
	}

	@Test
	@DisplayName("플레이리스트내의 영상 제거 성공")
	void deleteVideo() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);
		int videoIndex = 0;

		given(jwtService.isValidToken(eq(token)))
			.willReturn(true);
		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		doNothing()
			.when(playlistService).deleteVideo(eq(1L), eq(videoIndex));

		// when // then
		mockMvc.perform(delete("/playlists/{index}", videoIndex)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.PLAYLIST_DELETE_SUCCESS.getDescription()))
			.andDo(document("delete-playlist-video-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
				)
			));
	}

	@Test
	@DisplayName("플레이리스트내의 영상 제거 실패: 유효하지 않은 인덱스의 영상입니다")
	void deleteVideoFail() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);
		int videoIndex = 0;

		given(jwtService.isValidToken(eq(token)))
			.willReturn(true);
		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		doThrow(new InvalidVideoOrderException())
			.when(playlistService).deleteVideo(eq(1L), eq(videoIndex));

		// when // then
		mockMvc.perform(delete("/playlists/{index}", videoIndex)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(document("delete-playlist-video-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터"),
					fieldWithPath("data[].type").type(JsonFieldType.STRING).description("오류 타입"),
					fieldWithPath("data[].message").type(JsonFieldType.STRING).description("오류 메시지")
				)
			));
	}

}
