package site.youtogether.user.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static site.youtogether.exception.ErrorType.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.youtogether.RestDocsSupport;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.user.dto.UpdateNicknameForm;
import site.youtogether.user.dto.UserNickname;
import site.youtogether.util.api.ResponseResult;

class UserControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("유저 닉네임 가져오기 성공")
	void fetchUserNickname() throws Exception {
		// given
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), "a85192c998454a1ea055");
		UserNickname userNickname = new UserNickname("개구장이");
		given(userService.fetchUserNickname(sessionCookie.getValue()))
			.willReturn(userNickname);

		// when // then
		mockMvc.perform(get("/user/nickname")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.USER_NICKNAME_FETCH_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.nickname").value(userNickname.getNickname()))
			.andDo(document("fetch-user-nickname-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임")
				)
			));
	}

	@Test
	@DisplayName("유저 닉네임 가져오기 실패: 존재하지 않는 유저입니다")
	void fetchUserNicknameFail() throws Exception {
		// given
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), "a85192c998454a1ea055");
		given(userService.fetchUserNickname(sessionCookie.getValue()))
			.willThrow(new UserNoExistenceException());

		// when // then
		mockMvc.perform(get("/user/nickname")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.EXCEPTION_OCCURRED.getDescription()))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].type").value(UserNoExistenceException.class.getSimpleName()))
			.andExpect(jsonPath("$.data[0].message").value(USER_NO_EXISTENCE.getMessage()))
			.andDo(document("fetch-user-nickname-fail",
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
	@DisplayName("유저 닉네임 변경하기 성공")
	void updateUserNickname() throws Exception {
		// given
		Cookie sessionCookie = new Cookie(cookieProperties.getName(), "a85192c998454a1ea055");

		UpdateNicknameForm form = new UpdateNicknameForm("안개구장이");
		UserNickname userNickname = new UserNickname(form.getUpdateName());
		given(userService.updateUserNickname(sessionCookie.getValue(), form.getUpdateName()))
			.willReturn(userNickname);

		// when // then
		mockMvc.perform(patch("/user/nickname")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(form))
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
			.andExpect(jsonPath("$.result").value(ResponseResult.USER_NICKNAME_UPDATE_SUCCESS.getDescription()))
			.andExpect(jsonPath("$.data.nickname").value(form.getUpdateName()))
			.andDo(document("update-user-nickname-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("updateName").type(JsonFieldType.STRING).description("변경할 닉네임")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("result").type(JsonFieldType.STRING).description("결과"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임")
				)
			));
		
	}

}
