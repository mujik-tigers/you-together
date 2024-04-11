package site.youtogether.user.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.youtogether.RestDocsSupport;
import site.youtogether.user.Role;
import site.youtogether.user.dto.UpdateUserForm;
import site.youtogether.user.dto.UserInfo;
import site.youtogether.util.api.ResponseResult;

class UserControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("닉네임 변경 성공")
	void updateNickname() throws Exception {
		// given
		String updateNickname = "내가 바로 진짜 황똥땡";
		String roomCode = "c98780fe33";
		Long userId = 1L;
		UpdateUserForm form = new UpdateUserForm(roomCode, updateNickname);
		UserInfo userInfo = new UserInfo(userId, updateNickname, Role.GUEST);
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), "a85192c998454a1ea055");

		given(userTrackingStorage.findByCookieValue(eq(sessionCookie.getValue())))        // ArgumentResolver 에서 사용
			.willReturn(Optional.of(userId));

		given(userService.updateUserNickname(eq(userId), eq(updateNickname), eq(roomCode)))
			.willReturn(userInfo);

		// when // then
		mockMvc.perform(patch("/users")
				.content(objectMapper.writeValueAsString(form))
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.USER_NICKNAME_UPDATE_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.userId").value(userId))
			.andExpect(jsonPath("$.data.nickname").value(updateNickname))
			.andExpect(jsonPath("$.data.role").value(userInfo.getRole().name()))
			.andDo(document("update-nickname-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("roomCode").type(JsonFieldType.STRING).description("방 코드"),
					fieldWithPath("updateNickname").type(JsonFieldType.STRING).description("변경할 닉네임")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("유저 아이디"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("변경된 닉네임"),
					fieldWithPath("data.role").type(JsonFieldType.STRING).description("등급")
				)
			));
	}

	@Test
	@DisplayName("닉네임 변경 실패: 요청 데이터 오류가 발생했습니다")
	void updateNicknameFail() throws Exception {
		// given
		String updateNickname = "";
		String roomCode = "c98780fe33";
		Long userId = 1L;
		UpdateUserForm form = new UpdateUserForm(roomCode, updateNickname);
		UserInfo userInfo = new UserInfo(userId, updateNickname, Role.GUEST);
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), "a85192c998454a1ea055");

		given(userTrackingStorage.findByCookieValue(eq(sessionCookie.getValue())))        // ArgumentResolver 에서 사용
			.willReturn(Optional.of(userId));

		given(userService.updateUserNickname(eq(userId), eq(updateNickname), eq(roomCode)))
			.willReturn(userInfo);

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
			.andDo(document("update-nickname-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("roomCode").type(JsonFieldType.STRING).description("방 코드"),
					fieldWithPath("updateNickname").type(JsonFieldType.STRING).description("변경할 닉네임")
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
