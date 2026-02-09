package com.giftify.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giftify.common.exception.GlobalExceptionHandler;
import com.giftify.member.adapter.in.web.controller.MemberController;
import com.giftify.member.adapter.in.web.dto.request.RegisterMemberRequest;
import com.giftify.member.application.port.in.MemberUseCase;
import com.giftify.member.application.port.in.RegisterMemberCommand;
import com.giftify.member.core.exception.MemberDomainException;
import com.giftify.member.core.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MemberUseCase memberUseCase;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setUp() {
        org.springframework.validation.beanvalidation.LocalValidatorFactoryBean validator = new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("회원가입 API 호출 성공")
    void signup_success() throws Exception {
        // given
        RegisterMemberRequest request = new RegisterMemberRequest(
                "nickname",
                "password123",
                "test@example.com",
                "name",
                "010-1234-5678",
                "address",
                "2000-01-01",
                "image",
                false
        );
        given(memberUseCase.registerMember(any(RegisterMemberCommand.class))).willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.memberId").value(1L))
                .andExpect(jsonPath("$.data.message").value("회원가입이 완료되었습니다."));
    }

    @Test
    @DisplayName("회원가입 API 호출 실패 - 유효하지 않은 입력")
    void signup_fail_invalidInput() throws Exception {
        // given
        RegisterMemberRequest request = new RegisterMemberRequest(
                "", // 빈 닉네임
                "short", // 짧은 비밀번호
                "invalid-email", // 잘못된 이메일 형식
                "name",
                "010-1234-5678",
                "address",
                "2000-01-01",
                "image",
                false
        );

        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 API 호출 실패 - 이미 존재하는 이메일")
    void signup_fail_duplicateEmail() throws Exception {
        // given
        RegisterMemberRequest request = new RegisterMemberRequest(
                "nickname", "password123", "duplicate@example.com", "name",
                "010-1234-5678", "address", "2000-01-01", "image", false
        );
        given(memberUseCase.registerMember(any(RegisterMemberCommand.class)))
                .willThrow(new MemberDomainException(MemberErrorCode.DUPLICATED_EMAIL));

        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(MemberErrorCode.DUPLICATED_EMAIL.getCode()))
                .andExpect(jsonPath("$.error.message").value(MemberErrorCode.DUPLICATED_EMAIL.getMessage()));
    }
}
