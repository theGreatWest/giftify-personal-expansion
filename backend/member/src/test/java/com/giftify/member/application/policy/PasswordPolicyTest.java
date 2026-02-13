package com.giftify.member.application.policy;

import com.giftify.member.core.exception.MemberBusinessException;
import com.giftify.member.core.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordPolicyTest {

    private final PasswordPolicy passwordPolicy = new PasswordPolicy();

    @Test
    @DisplayName("비밀번호 검증 성공 - 대문자, 소문자, 특수문자 포함")
    void validate_success() {
        // given
        SignupContext context = new SignupContext("tester", "Password@123", "test@example.com", "010-1234-5678");

        // when & then
        assertThatNoException().isThrownBy(() -> passwordPolicy.validate(context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"password123", "PASSWORD123", "Password123", "password@", "PASSWORD@"})
    @DisplayName("비밀번호 검증 실패 - 조건 미충족")
    void validate_fail_weakPassword(String weakPassword) {
        // given
        SignupContext context = new SignupContext("tester", weakPassword, "test@example.com", "010-1234-5678");

        // when & then
        assertThatThrownBy(() -> passwordPolicy.validate(context))
                .isInstanceOf(MemberBusinessException.class)
                .hasMessage(MemberErrorCode.WEAK_PASSWORD.getMessage());
    }
}
