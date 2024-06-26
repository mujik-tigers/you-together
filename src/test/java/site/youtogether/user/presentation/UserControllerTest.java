package site.youtogether.user.presentation;

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
import site.youtogether.exception.ErrorType;
import site.youtogether.exception.user.HigherOrEqualRoleChangeException;
import site.youtogether.exception.user.HigherOrEqualRoleUserChangeException;
import site.youtogether.exception.user.NotManageableUserException;
import site.youtogether.exception.user.SelfRoleChangeException;
import site.youtogether.exception.user.UserNicknameDuplicateException;
import site.youtogether.room.Participant;
import site.youtogether.user.Role;
import site.youtogether.user.dto.NicknameDuplicationFlag;
import site.youtogether.user.dto.NicknameInput;
import site.youtogether.user.dto.UserRoleChangeForm;
import site.youtogether.util.api.ResponseResult;

class UserControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("닉네임 중복 검사 성공 : 사용 가능한 닉네임입니다")
	void checkNicknameDuplication() throws Exception {
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user nickname for request
		String newNickname = "new nickname";

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userService.checkUserNicknameDuplication(any()))
			.willReturn(new NicknameDuplicationFlag(true));

		// when // then
		mockMvc.perform(get("/users/nicknames/check")
				.param("nickname", newNickname)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.USER_NICKNAME_DUPLICATION_CHECK_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.nicknameIsUnique").value(true))
			.andDo(document("check-nickname-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.nicknameIsUnique").type(JsonFieldType.BOOLEAN).description("사용 가능 여부")
				)
			));
	}

	@Test
	@DisplayName("닉네임 중복 검사 성공 : 이미 사용 중인 닉네임입니다")
	void checkNicknameDuplicationFail() throws Exception {
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user nickname for request
		String newNickname = "new nickname";

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userService.checkUserNicknameDuplication(any()))
			.willReturn(new NicknameDuplicationFlag(false));

		// when // then
		mockMvc.perform(get("/users/nicknames/check")
				.param("nickname", newNickname)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.USER_NICKNAME_DUPLICATION_CHECK_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.nicknameIsUnique").value(false))
			.andDo(document("check-nickname-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.nicknameIsUnique").type(JsonFieldType.BOOLEAN).description("사용 가능 여부")
				)
			));
	}

	@Test
	@DisplayName("닉네임 변경 성공")
	void changeNickname() throws Exception {
		// given
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user nickname for request
		String newNickname = "new nickname";
		NicknameInput form = new NicknameInput(newNickname);

		// Setting up response data
		Participant participant = new Participant(1L, newNickname, Role.GUEST);

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userStorage.existsById(eq(1L)))
			.willReturn(true);
		given(userService.changeUserNickname(eq(participant.getUserId()), eq(newNickname)))
			.willReturn(participant);

		// when // then
		mockMvc.perform(patch("/users")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.USER_NICKNAME_CHANGE_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.userId").value(participant.getUserId()))
			.andExpect(jsonPath("$.data.nickname").value(newNickname))
			.andExpect(jsonPath("$.data.role").value(participant.getRole().name()))
			.andDo(document("change-nickname-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("newNickname").type(JsonFieldType.STRING).description("새로운 닉네임")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("새로운 닉네임"),
					fieldWithPath("data.role").type(JsonFieldType.STRING).description("역할")
				)
			));
	}

	@Test
	@DisplayName("닉네임 변경 실패: 요청 데이터 오류가 발생했습니다")
	void changeNicknameFail() throws Exception {
		// given
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user nickname for request
		String newNickname = " ";
		NicknameInput form = new NicknameInput(newNickname);

		// Setting up response data
		Participant participant = new Participant(1L, newNickname, Role.GUEST);

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userStorage.existsById(eq(1L)))
			.willReturn(true);
		given(userService.changeUserNickname(eq(participant.getUserId()), eq(newNickname)))
			.willReturn(participant);

		// when // then
		mockMvc.perform(patch("/users")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(document("change-nickname-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("newNickname").type(JsonFieldType.STRING).description("새로운 닉네임")
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
	@DisplayName("닉네임 변경 실패: 중복된 닉네임입니다")
	void changeNicknameFailDuplicate() throws Exception {
		// given
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user nickname for request
		String newNickname = "hyun";
		NicknameInput form = new NicknameInput(newNickname);

		// Setting up response data
		Participant participant = new Participant(1L, newNickname, Role.GUEST);

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userStorage.existsById(eq(1L)))
			.willReturn(true);
		given(userService.changeUserNickname(eq(participant.getUserId()), eq(newNickname)))
			.willThrow(new UserNicknameDuplicateException());

		// when // then
		mockMvc.perform(patch("/users")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(UserNicknameDuplicateException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(ErrorType.USER_NICKNAME_DUPLICATE.getMessage()))
			.andDo(document("change-nickname-fail-duplicate",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("newNickname").type(JsonFieldType.STRING).description("새로운 닉네임")
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
	@DisplayName("다른 유저의 역할 변경 성공")
	void changeRole() throws Exception {
		// given
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user role for request
		Long targetUserId = 2L;
		Role newUserRole = Role.VIEWER;
		UserRoleChangeForm form = new UserRoleChangeForm(targetUserId, newUserRole);

		// Setting up response data
		Participant participant = new Participant(form.getTargetUserId(), "hyun", form.getNewUserRole());

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userStorage.existsById(eq(1L)))
			.willReturn(true);
		given(userService.changeUserRole(eq(1L), any(UserRoleChangeForm.class)))
			.willReturn(participant);

		// when // then
		mockMvc.perform(patch("/users/role")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.USER_ROLE_CHANGE_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.userId").value(targetUserId))
			.andExpect(jsonPath("$.data.role").value(Role.VIEWER.name()))
			.andDo(document("change-role-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("targetUserId").type(JsonFieldType.NUMBER).description("변경할 유저의 아이디"),
					fieldWithPath("newUserRole").type(JsonFieldType.STRING).description("새로운 역할")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
					fieldWithPath("data.role").type(JsonFieldType.STRING).description("새로운 역할")
				)
			));
	}

	@Test
	@DisplayName("다른 유저의 역할 변경 실패: 자신의 역할은 변경할 수 없습니다")
	void changeRoleFail_Self() throws Exception {
		// given
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user role for request
		Long targetUserId = 1L;
		Role newUserRole = Role.VIEWER;
		UserRoleChangeForm form = new UserRoleChangeForm(targetUserId, newUserRole);

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userStorage.existsById(eq(1L)))
			.willReturn(true);
		given(userService.changeUserRole(eq(targetUserId), any(UserRoleChangeForm.class)))
			.willThrow(new SelfRoleChangeException());

		// when // then
		mockMvc.perform(patch("/users/role")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(SelfRoleChangeException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(ErrorType.SELF_ROLE_CHANGE.getMessage()))
			.andDo(document("change-role-fail-self",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("targetUserId").type(JsonFieldType.NUMBER).description("변경할 유저의 아이디"),
					fieldWithPath("newUserRole").type(JsonFieldType.STRING).description("새로운 역할")
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
	@DisplayName("다른 유저의 역할 변경 실패: 자신과 동등하거나 높은 단계의 유저에 대한 역할을 변경할 수 없습니다")
	void changeRoleFail_EqualOrHigherUser() throws Exception {
		// given
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user role for request
		Long targetUserId = 2L;
		Role newUserRole = Role.VIEWER;
		UserRoleChangeForm form = new UserRoleChangeForm(targetUserId, newUserRole);

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userStorage.existsById(eq(1L)))
			.willReturn(true);
		given(userService.changeUserRole(eq(1L), any(UserRoleChangeForm.class)))
			.willThrow(new HigherOrEqualRoleUserChangeException());

		// when // then
		mockMvc.perform(patch("/users/role")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(HigherOrEqualRoleUserChangeException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(ErrorType.HIGHER_OR_EQUAL_USER_ROLE_CHANGE.getMessage()))
			.andDo(document("change-role-fail-equal-or-higher-user",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("targetUserId").type(JsonFieldType.NUMBER).description("변경할 유저의 아이디"),
					fieldWithPath("newUserRole").type(JsonFieldType.STRING).description("새로운 역할")
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
	@DisplayName("다른 유저의 역할 변경 실패: 자신의 역할보다 높은 단계의 역할로 변경할 수 없습니다")
	void changeRoleFail_HigherRole() throws Exception {
		// given
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user role for request
		Long targetUserId = 2L;
		Role newUserRole = Role.HOST;
		UserRoleChangeForm form = new UserRoleChangeForm(targetUserId, newUserRole);

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userStorage.existsById(eq(1L)))
			.willReturn(true);
		given(userService.changeUserRole(eq(1L), any(UserRoleChangeForm.class)))
			.willThrow(new HigherOrEqualRoleChangeException());

		// when // then
		mockMvc.perform(patch("/users/role")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(HigherOrEqualRoleChangeException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(ErrorType.HIGHER_OR_EQUAL_ROLE_CHANGE.getMessage()))
			.andDo(document("change-role-fail-higher-role",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("targetUserId").type(JsonFieldType.NUMBER).description("변경할 유저의 아이디"),
					fieldWithPath("newUserRole").type(JsonFieldType.STRING).description("새로운 역할")
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
	@DisplayName("다른 유저의 역할 변경 실패: MANAGER보다 낮은 단계의 유저는 다른 유저의 역할을 변경할 수 없습니다")
	void changeRoleFail_NotManageableUser() throws Exception {
		// given
		// given
		// Setting session cookie for request
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSJ9.XJHPNpgWMty0iKr1FQKCBeOapvlqk1RjcPQUzT2dFlA";
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), token);

		// Setting new user role for request
		Long targetUserId = 2L;
		Role newUserRole = Role.GUEST;
		UserRoleChangeForm form = new UserRoleChangeForm(targetUserId, newUserRole);

		given(jwtService.parse(eq(token)))
			.willReturn(1L);
		given(userStorage.existsById(eq(1L)))
			.willReturn(true);
		given(userService.changeUserRole(eq(1L), any(UserRoleChangeForm.class)))
			.willThrow(new NotManageableUserException());

		// when // then
		mockMvc.perform(patch("/users/role")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(NotManageableUserException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(ErrorType.NOT_MANAGEABLE.getMessage()))
			.andDo(document("change-role-fail-not-manageable-user",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("targetUserId").type(JsonFieldType.NUMBER).description("변경할 유저의 아이디"),
					fieldWithPath("newUserRole").type(JsonFieldType.STRING).description("새로운 역할")
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

}
