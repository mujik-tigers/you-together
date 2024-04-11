package site.youtogether.room.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static site.youtogether.exception.ErrorType.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.youtogether.RestDocsSupport;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.room.Room;
import site.youtogether.room.dto.RoomDetail;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.util.api.ResponseResult;

class RoomControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("방 생성 성공")
	void createRoomSuccess() throws Exception {
		// given
		// Setting up request data for creating a room
		String roomCode = "1e7050f7d7";
		String roomTitle = "재밌는 쇼츠 같이 보기";
		String hostNickname = "황똥땡";
		int capacity = 10;

		RoomSettings roomSettings = RoomSettings.builder().capacity(capacity).title(roomTitle).password(null).build();

		// Setting up response data for the created room
		RoomDetail createdRoomDetail = new RoomDetail(roomCode, roomTitle, hostNickname, capacity, 1, false);
		given(roomService.create(anyString(), any(RoomSettings.class), any(LocalDateTime.class))).willReturn(createdRoomDetail);

		// when / then
		String cookieName = cookieProperties.getName();

		mockMvc.perform(post("/rooms")
				.content(objectMapper.writeValueAsString(roomSettings))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(cookie().exists(cookieName))
			.andExpect(cookie().domain(cookieName, cookieProperties.getDomain()))
			.andExpect(cookie().path(cookieName, cookieProperties.getPath()))
			.andExpect(cookie().sameSite(cookieName, cookieProperties.getSameSite()))
			.andExpect(cookie().maxAge(cookieName, cookieProperties.getExpiry()))
			.andExpect(cookie().httpOnly(cookieName, true))
			.andExpect(cookie().secure(cookieName, true))
			.andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_CREATION_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.roomCode").value(roomCode))
			.andExpect(jsonPath("$.data.roomTitle").value(roomTitle))
			.andExpect(jsonPath("$.data.nickname").value(hostNickname))
			.andExpect(jsonPath("$.data.capacity").value(capacity))
			.andExpect(jsonPath("$.data.currentParticipant").value(1))
			.andExpect(jsonPath("$.data.passwordExist").value(false))
			.andDo(document("create-room-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("capacity").type(JsonFieldType.NUMBER).description("정원"),
					fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.roomCode").type(JsonFieldType.STRING).description("방 식별 코드"),
					fieldWithPath("data.roomTitle").type(JsonFieldType.STRING).description("방 제목"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("내 닉네임"),
					fieldWithPath("data.capacity").type(JsonFieldType.NUMBER).description("정원"),
					fieldWithPath("data.currentParticipant").type(JsonFieldType.NUMBER).description("현재 참가자 수"),
					fieldWithPath("data.passwordExist").type(JsonFieldType.BOOLEAN).description("비밀번호 존재 여부")
				)
			));
	}

	@Test
	@DisplayName("방 생성 실패: 요청 데이터 오류가 발생했습니다")
	void createRoomFail_RoomSettingError() throws Exception {
		// given
		// Setting up request data for creating a room
		RoomSettings roomSettings = RoomSettings.builder().capacity(11).title(" ").password("a1b2").build();

		// when / then
		mockMvc.perform(post("/rooms")
				.content(objectMapper.writeValueAsString(roomSettings))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(cookie().doesNotExist(cookieProperties.getName()))
			.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(document("create-room-fail-room-setting-error",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("capacity").type(JsonFieldType.NUMBER).description("정원"),
					fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional()),
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
	@DisplayName("방 생성 실패: 다수의 방에 참가할 수 없습니다")
	void createRoomFail_SingleRoomParticipantViolation() throws Exception {
		// given
		// Setting up session cookie and request data for creating a room
		// This indicates that a session cookie is already present, implying participation in a room
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), "a85192c998454a1ea055");
		RoomSettings roomSettings = RoomSettings.builder().capacity(10).title("재밌는 쇼츠 같이 보기").password(null).build();

		// Setting up user tracking storage for interceptor
		given(userTrackingStorage.exists(anyString())).willReturn(true);

		// when / then
		mockMvc.perform(post("/rooms")
				.content(objectMapper.writeValueAsString(roomSettings))
				.contentType(MediaType.APPLICATION_JSON).cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(cookie().doesNotExist(cookieProperties.getName()))
			.andExpect(jsonPath("$.code").value(SINGLE_ROOM_PARTICIPATION_VIOLATION.getStatus().value()))
			.andExpect(jsonPath("$.status").value(SINGLE_ROOM_PARTICIPATION_VIOLATION.getStatus().getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(SingleRoomParticipationViolationException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(SINGLE_ROOM_PARTICIPATION_VIOLATION.getMessage()))
			.andDo(document("create-room-fail-single-room-participant-violation",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("capacity").type(JsonFieldType.NUMBER).description("정원"),
					fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional()),
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
	@DisplayName("방 목록 조회 성공")
	void fetchRoomListSuccess() throws Exception {
		// given
		// Setting up response data for the fetched room list
		SliceImpl<Room> roomSlice = new SliceImpl<>(generateRooms(3), PageRequest.of(0, 10), false);
		RoomList roomList = new RoomList(roomSlice);
		given(roomService.fetchAll(any(Pageable.class), anyString())).willReturn(roomList);

		// when / then
		mockMvc.perform(get("/rooms")
				.param("page", "0")
				.param("size", "10")
				.param("keyword", "침착"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_LIST_FETCH_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_LIST_FETCH_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.pageNumber").value(roomList.getPageNumber()))
			.andExpect(jsonPath("$.data.pageSize").value(roomList.getPageSize()))
			.andExpect(jsonPath("$.data.hasNext").value(roomList.isHasNext()))
			.andExpect(jsonPath("$.data.rooms[0].roomCode").value(roomList.getRooms().get(0).getRoomCode()))
			.andExpect(jsonPath("$.data.rooms[0].roomTitle").value(roomList.getRooms().get(0).getRoomTitle()))
			.andExpect(jsonPath("$.data.rooms[0].videoTitle").value(roomList.getRooms().get(0).getVideoTitle()))
			.andExpect(jsonPath("$.data.rooms[0].videoThumbnail").value(roomList.getRooms().get(0).getVideoThumbnail()))
			.andExpect(jsonPath("$.data.rooms[0].capacity").value(roomList.getRooms().get(0).getCapacity()))
			.andExpect(jsonPath("$.data.rooms[0].currentParticipant").value(roomList.getRooms().get(0).getCurrentParticipant()))
			.andExpect(jsonPath("$.data.rooms[0].passwordExist").value(roomList.getRooms().get(0).isPasswordExist()))
			.andDo(document("fetch-room-list-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지"),
					fieldWithPath("data.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
					fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
					fieldWithPath("data.rooms").type(JsonFieldType.ARRAY).description("방 목록 조회 결과"),
					fieldWithPath("data.rooms[].roomCode").type(JsonFieldType.STRING).description("방 식별 코드"),
					fieldWithPath("data.rooms[].roomTitle").type(JsonFieldType.STRING).description("방 제목"),
					fieldWithPath("data.rooms[].videoTitle").type(JsonFieldType.STRING).description("영상 제목"),
					fieldWithPath("data.rooms[].videoThumbnail").type(JsonFieldType.STRING).description("영상 썸네일 URL"),
					fieldWithPath("data.rooms[].capacity").type(JsonFieldType.NUMBER).description("정원"),
					fieldWithPath("data.rooms[].currentParticipant").type(JsonFieldType.NUMBER).description("현재 참여자 수"),
					fieldWithPath("data.rooms[].passwordExist").type(JsonFieldType.BOOLEAN).description("비밀번호 존재 여부")
				)
			));
	}

	@Test
	@DisplayName("방 입장 성공")
	void enterRoom() throws Exception {
		// given
		String roomCode = "1e7050f7d7";
		String roomTitle = "재밌는 쇼츠 같이 보기";
		String nickname = "황똥땡";
		int capacity = 10;

		RoomDetail createdRoomDetail = new RoomDetail(roomCode, roomTitle, nickname, capacity, 2, false);
		given(roomService.enter(anyString(), eq(roomCode)))
			.willReturn(createdRoomDetail);

		String cookieName = cookieProperties.getName();

		// when // then
		mockMvc.perform(post("/rooms/{roomCode}", roomCode))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(cookie().exists(cookieName))
			.andExpect(cookie().domain(cookieName, cookieProperties.getDomain()))
			.andExpect(cookie().path(cookieName, cookieProperties.getPath()))
			.andExpect(cookie().sameSite(cookieName, cookieProperties.getSameSite()))
			.andExpect(cookie().maxAge(cookieName, cookieProperties.getExpiry()))
			.andExpect(cookie().httpOnly(cookieName, true))
			.andExpect(cookie().secure(cookieName, true))
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_ENTER_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.roomCode").value(roomCode))
			.andExpect(jsonPath("$.data.roomTitle").value(roomTitle))
			.andExpect(jsonPath("$.data.nickname").value(nickname))
			.andExpect(jsonPath("$.data.capacity").value(capacity))
			.andExpect(jsonPath("$.data.currentParticipant").value(2))
			.andExpect(jsonPath("$.data.passwordExist").value(false))
			.andDo(document("enter-room-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.roomCode").type(JsonFieldType.STRING).description("방 식별 코드"),
					fieldWithPath("data.roomTitle").type(JsonFieldType.STRING).description("방 제목"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("내 닉네임"),
					fieldWithPath("data.capacity").type(JsonFieldType.NUMBER).description("정원"),
					fieldWithPath("data.currentParticipant").type(JsonFieldType.NUMBER).description("현재 참가자 수"),
					fieldWithPath("data.passwordExist").type(JsonFieldType.BOOLEAN).description("비밀번호 존재 여부")
				)
			));
	}

	@Test
	@DisplayName("방 입장 실패: 다수의 방에 참가할 수 없습니다")
	void enterRoomFail() throws Exception {
		// given
		String roomCode = "1e7050f7d7";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), "a85192c998454a1ea055");
		given(userTrackingStorage.exists(anyString())).willReturn(true);

		// when // then
		mockMvc.perform(post("/rooms/{roomCode}", roomCode)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(cookie().doesNotExist(cookieProperties.getName()))
			.andExpect(jsonPath("$.code").value(SINGLE_ROOM_PARTICIPATION_VIOLATION.getStatus().value()))
			.andExpect(jsonPath("$.status").value(SINGLE_ROOM_PARTICIPATION_VIOLATION.getStatus().getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(SingleRoomParticipationViolationException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(SINGLE_ROOM_PARTICIPATION_VIOLATION.getMessage()))
			.andDo(document("enter-room-fail-single-room-participant-violation",
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

	private List<Room> generateRooms(int count) {
		User host = User.builder().userId(1L).role(Role.HOST).nickname("연츠비").build();

		return IntStream.rangeClosed(1, count)
			.mapToObj(number -> Room.builder()
				.title("2023년 침착맨 정주행 " + number)
				.capacity(10)
				.host(host)
				.createdAt(LocalDateTime.of(2024, 4, 6, 19, 43, 0))
				.build())
			.toList();
	}

}
