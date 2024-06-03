package site.youtogether.room.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static site.youtogether.exception.ErrorType.*;
import static site.youtogether.util.AppConstants.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.youtogether.RestDocsSupport;
import site.youtogether.exception.room.PasswordNotMatchException;
import site.youtogether.exception.room.RoomCapacityExceededException;
import site.youtogether.exception.room.SingleRoomParticipationViolationException;
import site.youtogether.exception.user.ChangeRoomTitleDeniedException;
import site.youtogether.room.Participant;
import site.youtogether.room.Room;
import site.youtogether.room.dto.ChangedRoomTitle;
import site.youtogether.room.dto.NewRoom;
import site.youtogether.room.dto.PasswordInput;
import site.youtogether.room.dto.RoomDetail;
import site.youtogether.room.dto.RoomList;
import site.youtogether.room.dto.RoomListDetail;
import site.youtogether.room.dto.RoomSettings;
import site.youtogether.room.dto.TitleInput;
import site.youtogether.user.Role;
import site.youtogether.user.User;
import site.youtogether.util.RandomUtil;
import site.youtogether.util.api.ResponseResult;

class RoomControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("방 생성 성공")
	void createRoomSuccess() throws Exception {
		// given
		// Setting up request data for creating a room
		String roomCode = "1e7050f7d7";
		String roomTitle = "재밌는 쇼츠 같이 보기";
		int capacity = 10;
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";

		RoomSettings roomSettings = RoomSettings.builder()
			.capacity(capacity)
			.title(roomTitle)
			.password(null)
			.build();
		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode(null)
			.build());

		given(jwtService.issue(anyLong(), any()))
			.willReturn(token);
		given(userStorage.findById(anyLong()))
			.willReturn(user);

		// Setting up response data for the created room
		NewRoom newRoom = new NewRoom(roomCode, null);
		given(roomService.create(anyLong(), any(RoomSettings.class), any(LocalDateTime.class)))
			.willReturn(newRoom);

		// when / then
		mockMvc.perform(post("/rooms")
				.content(objectMapper.writeValueAsString(roomSettings))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(cookie().exists(cookieProperties.getName()))
			.andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_CREATION_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.roomCode").value(roomCode))
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
					fieldWithPath("data.password").type(JsonFieldType.NULL).description("방 비밀번호")
				)
			));
	}

	@Test
	@DisplayName("방 생성 성공: 비밀 번호가 있는 방")
	void createPasswordRoomSuccess() throws Exception {
		// given
		// Setting up request data for creating a room
		String roomCode = "1e7050f7d7";
		String roomTitle = "재밌는 쇼츠 같이 보기";
		String password = "mySecretRoom";
		int capacity = 10;
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";

		RoomSettings roomSettings = RoomSettings.builder()
			.capacity(capacity)
			.title(roomTitle)
			.password(password)
			.build();
		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode(null)
			.build());

		given(jwtService.issue(anyLong(), any()))
			.willReturn(token);
		given(userStorage.findById(anyLong()))
			.willReturn(user);

		// Setting up response data for the created room
		NewRoom newRoom = new NewRoom(roomCode, password);
		given(roomService.create(anyLong(), any(RoomSettings.class), any(LocalDateTime.class)))
			.willReturn(newRoom);

		// when / then
		mockMvc.perform(post("/rooms")
				.content(objectMapper.writeValueAsString(roomSettings))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(cookie().exists(cookieProperties.getName()))
			.andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_CREATION_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.roomCode").value(roomCode))
			.andExpect(jsonPath("$.data.password").value(password))
			.andDo(document("create-password-room-success",
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
					fieldWithPath("data.password").type(JsonFieldType.STRING).description("방 비밀번호")
				)
			));
	}

	@Test
	@DisplayName("방 생성 실패: 요청 데이터 오류가 발생했습니다")
	void createRoomFail_RoomSettingError() throws Exception {
		// given
		// Setting up request data for creating a room
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";

		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode(null)
			.build());
		RoomSettings roomSettings = RoomSettings
			.builder()
			.capacity(11)
			.title(" ")
			.password("a1b2")
			.build();

		given(jwtService.issue(anyLong(), any()))
			.willReturn(token);
		given(userStorage.findById(anyLong()))
			.willReturn(user);

		// when / then
		mockMvc.perform(post("/rooms")
				.content(objectMapper.writeValueAsString(roomSettings))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
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
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";

		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode("1e7050f7d7")
			.build());
		RoomSettings roomSettings = RoomSettings.builder()
			.capacity(10)
			.title("재밌는 쇼츠 같이 보기")
			.password(null)
			.build();

		given(jwtService.parse(token))
			.willReturn(1L);
		given(userStorage.findById(anyLong()))
			.willReturn(user);

		// when / then
		mockMvc.perform(post("/rooms")
				.content(objectMapper.writeValueAsString(roomSettings))
				.cookie(new Cookie(cookieProperties.getName(), token)))
			.andDo(print())
			.andExpect(status().isBadRequest())
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
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";

		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode("1e7050f7d7")
			.build());
		SliceImpl<Room> roomSlice = new SliceImpl<>(generateRooms(3), PageRequest.of(0, 10), false);

		RoomList roomList = new RoomList(roomSlice.getNumber(), roomSlice.getPageable().getPageSize(), roomSlice.hasNext(),
			createRoomListDetails(roomSlice));

		given(jwtService.parse(token))
			.willReturn(1L);
		given(userStorage.findById(anyLong()))
			.willReturn(user);
		given(roomService.fetchAll(any(Pageable.class), anyString())).willReturn(roomList);

		// when / then
		mockMvc.perform(get("/rooms")
				.param("page", "0")
				.param("size", "10")
				.param("keyword", "침착"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(cookie().exists(cookieProperties.getName()))
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_LIST_FETCH_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_LIST_FETCH_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.pageNumber").value(roomList.getPageNumber()))
			.andExpect(jsonPath("$.data.pageSize").value(roomList.getPageSize()))
			.andExpect(jsonPath("$.data.hasNext").value(roomList.isHasNext()))
			.andExpect(jsonPath("$.data.rooms[0].roomCode").value(roomList.getRooms().get(0).getRoomCode()))
			.andExpect(jsonPath("$.data.rooms[0].roomTitle").value(roomList.getRooms().get(0).getRoomTitle()))
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
					fieldWithPath("data.rooms[].videoTitle").type(JsonFieldType.STRING).description("영상 제목").optional(),
					fieldWithPath("data.rooms[].videoThumbnail").type(JsonFieldType.STRING).description("영상 썸네일 URL").optional(),
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
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		String roomCode = "1e7050f7d7";
		String roomTitle = "재밌는 쇼츠 같이 보기";
		int capacity = 10;

		Participant participantInfo = new Participant(10L, "황똥땡", Role.HOST);
		RoomDetail createdRoomDetail = new RoomDetail(roomCode, roomTitle, participantInfo, capacity, 2, false, "phuiiNCxRMg", "aespa 에스파 'Supernova' MV",
			"SMTOWN");
		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode(null)
			.build());

		given(jwtService.issue(anyLong(), any()))
			.willReturn(token);
		given(userStorage.findById(anyLong()))
			.willReturn(user);
		given(roomService.enter(eq(roomCode), anyLong(), eq(null)))
			.willReturn(createdRoomDetail);

		// when // then
		mockMvc.perform(post("/rooms/{roomCode}", roomCode))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(cookie().exists(cookieProperties.getName()))
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_ENTER_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.roomCode").value(roomCode))
			.andExpect(jsonPath("$.data.roomTitle").value(roomTitle))
			.andExpect(jsonPath("$.data.user.userId").value(participantInfo.getUserId()))
			.andExpect(jsonPath("$.data.capacity").value(capacity))
			.andExpect(jsonPath("$.data.currentParticipant").value(2))
			.andExpect(jsonPath("$.data.passwordExist").value(false))
			.andExpect(jsonPath("$.data.currentVideoTitle").value(createdRoomDetail.getCurrentVideoTitle()))
			.andExpect(jsonPath("$.data.currentChannelTitle").value(createdRoomDetail.getCurrentChannelTitle()))
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
					fieldWithPath("data.user").type(JsonFieldType.OBJECT).description("유저"),
					fieldWithPath("data.user.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
					fieldWithPath("data.user.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
					fieldWithPath("data.user.role").type(JsonFieldType.STRING).description("유저 역할"),
					fieldWithPath("data.capacity").type(JsonFieldType.NUMBER).description("정원"),
					fieldWithPath("data.currentParticipant").type(JsonFieldType.NUMBER).description("현재 참가자 수"),
					fieldWithPath("data.passwordExist").type(JsonFieldType.BOOLEAN).description("비밀번호 존재 여부"),
					fieldWithPath("data.currentVideoId").type(JsonFieldType.STRING).description("현재 재생중인 영상의 아이디").optional(),
					fieldWithPath("data.currentVideoTitle").type(JsonFieldType.STRING).description("현재 재생중인 영상의 제목").optional(),
					fieldWithPath("data.currentChannelTitle").type(JsonFieldType.STRING).description("현재 재생중인 영상의 채널 이름").optional()
				)
			));
	}

	@Test
	@DisplayName("방 입장 실패: 다수의 방에 참가할 수 없습니다")
	void enterRoomFail() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		String roomCode = "1e7050f7d7";
		String roomTitle = "재밌는 쇼츠 같이 보기";
		int capacity = 10;

		Participant participantInfo = new Participant(10L, "황똥땡", Role.HOST);
		RoomDetail createdRoomDetail = new RoomDetail(roomCode, roomTitle, participantInfo, capacity, 2, false, "phuiiNCxRMg", "aespa 에스파 'Supernova' MV",
			"SMTOWN");
		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode("1e7050f7d7")
			.build());

		given(jwtService.issue(anyLong(), any()))
			.willReturn(token);
		given(userStorage.findById(anyLong()))
			.willReturn(user);
		given(roomService.enter(eq(roomCode), anyLong(), eq(null)))
			.willReturn(createdRoomDetail);

		// when // then
		mockMvc.perform(post("/rooms/{roomCode}", roomCode))
			.andDo(print())
			.andExpect(status().isBadRequest())
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

	@Test
	@DisplayName("방 입장 성공: 비밀번호가 있는 방")
	void enterPasswordRoom() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		String roomCode = "1e7050f7d7";
		String roomTitle = "재밌는 쇼츠 같이 보기";
		String password = "mySecretRoom";
		int capacity = 10;

		Participant participantInfo = new Participant(10L, "황똥땡", Role.HOST);
		RoomDetail createdRoomDetail = new RoomDetail(roomCode, roomTitle, participantInfo, capacity, 2, true, "phuiiNCxRMg", "aespa 에스파 'Supernova' MV",
			"SMTOWN");
		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode(null)
			.build());

		given(jwtService.issue(anyLong(), any()))
			.willReturn(token);
		given(userStorage.findById(anyLong()))
			.willReturn(user);
		given(roomService.enter(eq(roomCode), anyLong(), eq(password)))
			.willReturn(createdRoomDetail);

		// when // then
		mockMvc.perform(post("/rooms/{roomCode}", roomCode)
				.content(objectMapper.writeValueAsString(new PasswordInput(password)))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(cookie().exists(cookieProperties.getName()))
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_ENTER_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.roomCode").value(roomCode))
			.andExpect(jsonPath("$.data.roomTitle").value(roomTitle))
			.andExpect(jsonPath("$.data.user.userId").value(participantInfo.getUserId()))
			.andExpect(jsonPath("$.data.capacity").value(capacity))
			.andExpect(jsonPath("$.data.currentParticipant").value(2))
			.andExpect(jsonPath("$.data.passwordExist").value(true))
			.andExpect(jsonPath("$.data.currentVideoTitle").value(createdRoomDetail.getCurrentVideoTitle()))
			.andExpect(jsonPath("$.data.currentChannelTitle").value(createdRoomDetail.getCurrentChannelTitle()))
			.andDo(document("enter-password-room-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("passwordInput").type(JsonFieldType.STRING).description("입력한 비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.roomCode").type(JsonFieldType.STRING).description("방 식별 코드"),
					fieldWithPath("data.roomTitle").type(JsonFieldType.STRING).description("방 제목"),
					fieldWithPath("data.user").type(JsonFieldType.OBJECT).description("유저"),
					fieldWithPath("data.user.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
					fieldWithPath("data.user.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
					fieldWithPath("data.user.role").type(JsonFieldType.STRING).description("유저 역할"),
					fieldWithPath("data.capacity").type(JsonFieldType.NUMBER).description("정원"),
					fieldWithPath("data.currentParticipant").type(JsonFieldType.NUMBER).description("현재 참가자 수"),
					fieldWithPath("data.passwordExist").type(JsonFieldType.BOOLEAN).description("비밀번호 존재 여부"),
					fieldWithPath("data.currentVideoId").type(JsonFieldType.STRING).description("현재 재생중인 영상의 아이디").optional(),
					fieldWithPath("data.currentVideoTitle").type(JsonFieldType.STRING).description("현재 재생중인 영상의 제목").optional(),
					fieldWithPath("data.currentChannelTitle").type(JsonFieldType.STRING).description("현재 재생중인 영상의 채널 이름").optional()
				)
			));
	}

	@Test
	@DisplayName("방 입장 실패: 비밀번호가 틀립니다")
	void enterPasswordRoomFail() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		String roomCode = "1e7050f7d7";
		String password = "notMatchPassword";

		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode(null)
			.build());

		given(jwtService.issue(anyLong(), any()))
			.willReturn(token);
		given(userStorage.findById(anyLong()))
			.willReturn(user);
		given(roomService.enter(eq(roomCode), anyLong(), eq(password)))
			.willThrow(new PasswordNotMatchException());

		// when // then
		mockMvc.perform(post("/rooms/{roomCode}", roomCode)
				.content(objectMapper.writeValueAsString(new PasswordInput(password)))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(ROOM_PASSWORD_NOT_MATCH.getStatus().value()))
			.andExpect(jsonPath("$.status").value(ROOM_PASSWORD_NOT_MATCH.getStatus().getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(PasswordNotMatchException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(ROOM_PASSWORD_NOT_MATCH.getMessage()))
			.andDo(document("enter-password-room-fail-password-wrong",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("passwordInput").type(JsonFieldType.STRING).description("입력한 비밀번호")
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
	@DisplayName("방 입장 실패: 비밀번호 형식 오류")
	void enterPasswordRoomFormFail() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		String roomCode = "1e7050f7d7";
		String password = " a ";

		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode(null)
			.build());

		given(jwtService.issue(anyLong(), any()))
			.willReturn(token);
		given(userStorage.findById(anyLong()))
			.willReturn(user);

		// when // then
		mockMvc.perform(post("/rooms/{roomCode}", roomCode)
				.content(objectMapper.writeValueAsString(new PasswordInput(password)))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(document("enter-password-room-fail-password-form",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("passwordInput").type(JsonFieldType.STRING).description("입력한 비밀번호")
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
	@DisplayName("방 입장 실패: 방의 참가 인원이 가득 참")
	void enterFullRoomFail() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		String roomCode = "1e7050f7d7";

		Optional<User> user = Optional.of(User.builder()
			.currentRoomCode(null)
			.build());

		given(jwtService.issue(anyLong(), any()))
			.willReturn(token);
		given(userStorage.findById(anyLong()))
			.willReturn(user);
		given(roomService.enter(eq(roomCode), anyLong(), eq(null)))
			.willThrow(new RoomCapacityExceededException());

		// when // then
		mockMvc.perform(post("/rooms/{roomCode}", roomCode))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(ROOM_CAPACITY_EXCEEDED.getStatus().value()))
			.andExpect(jsonPath("$.status").value(ROOM_CAPACITY_EXCEEDED.getStatus().getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(RoomCapacityExceededException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(ROOM_CAPACITY_EXCEEDED.getMessage()))
			.andDo(document("enter-full-room-fail",
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
	@DisplayName("방 제목 변경 성공")
	void updateRoomTitle() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		String roomCode = "1e7050f7d7";
		Long id = 10L;
		String newTitle = "연똥땡의 방";

		TitleInput form = new TitleInput(newTitle);

		given(jwtService.parse(anyString()))
			.willReturn(id);
		given(roomService.changeRoomTitle(anyLong(), anyString()))
			.willReturn(new ChangedRoomTitle(roomCode, newTitle));

		// when // then
		mockMvc.perform(patch("/rooms/title")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(new Cookie(cookieProperties.getName(), token)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.ROOM_TITLE_CHANGE_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.roomCode").value(roomCode))
			.andExpect(jsonPath("$.data.changedRoomTitle").value(newTitle))
			.andDo(document("change-room-title-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("newTitle").type(JsonFieldType.STRING).description("새로운 방 제목")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.roomCode").type(JsonFieldType.STRING).description("방 코드"),
					fieldWithPath("data.changedRoomTitle").type(JsonFieldType.STRING).description("방 제목")
				)
			));
	}

	@Test
	@DisplayName("방 제목 변경 실패: 요청한 데이터 형식 오류")
	void updateRoomTitleFailForm() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		String roomCode = "1e7050f7d7";
		Long id = 10L;
		String newTitle = "  ";

		TitleInput form = new TitleInput(newTitle);

		given(jwtService.parse(anyString()))
			.willReturn(id);
		given(roomService.changeRoomTitle(anyLong(), anyString()))
			.willReturn(new ChangedRoomTitle(roomCode, newTitle));

		// when // then
		mockMvc.perform(patch("/rooms/title")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(new Cookie(cookieProperties.getName(), token)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(document("change-room-title-form-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("newTitle").type(JsonFieldType.STRING).description("새로운 방 제목")
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
	@DisplayName("방 제목 변경 실패: 호스트가 아닌 유저는 방 제목 변경 불가")
	void updateRoomTitleFailNotHost() throws Exception {
		// given
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Long id = 10L;
		String newTitle = "연똥땡의 방";

		TitleInput form = new TitleInput(newTitle);

		given(jwtService.parse(anyString()))
			.willReturn(id);
		given(roomService.changeRoomTitle(anyLong(), anyString()))
			.willThrow(new ChangeRoomTitleDeniedException());

		// when // then
		mockMvc.perform(patch("/rooms/title")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(new Cookie(cookieProperties.getName(), token)))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(ChangeRoomTitleDeniedException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(ROOM_TITLE_CHANGE_DENIED.getMessage()))
			.andDo(document("change-room-title-not-host-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("newTitle").type(JsonFieldType.STRING).description("새로운 방 제목")
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

	private List<Room> generateRooms(int count) {
		return IntStream.rangeClosed(1, count)
			.mapToObj(number -> Room.builder()
				.code(RandomUtil.generateRandomCode(ROOM_CODE_LENGTH))
				.title("2023년 침착맨 정주행 " + number)
				.capacity(10)
				.createdAt(LocalDateTime.of(2024, 4, 6, 19, 43, 0))
				.build())
			.toList();
	}

	private List<RoomListDetail> createRoomListDetails(Slice<Room> roomSlice) {
		List<Room> rooms = roomSlice.getContent();
		List<RoomListDetail> roomListDetails = new ArrayList<>();
		for (int i = 0; i < rooms.size(); i++) {
			if (i == 2) {
				roomListDetails.add(new RoomListDetail(rooms.get(i).getCode(), rooms.get(i).getTitle(), null, null, rooms.get(i).getCapacity(),
					rooms.get(i).getParticipantCount(), rooms.get(i).hasPassword()));
			} else {
				roomListDetails.add(new RoomListDetail(rooms.get(i).getCode(), rooms.get(i).getTitle(), "궤도 '연애의 과학' 특강",
					"https://i.ytimg.com/vi/TXI1npEFNss/hqdefault.jpg", rooms.get(i).getCapacity(),
					rooms.get(i).getParticipantCount(), rooms.get(i).hasPassword()));
			}
		}
		return roomListDetails;
	}

}
