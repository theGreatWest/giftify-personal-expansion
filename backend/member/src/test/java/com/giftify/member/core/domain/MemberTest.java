package com.giftify.member.core.domain;

import com.giftify.member.core.exception.MemberDomainException;
import com.giftify.member.core.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("회원 생성 성공")
    void createMember_success() {
        // given
        String nickname = "tester";
        String password = "encryptedPassword";
        String email = "test@example.com";
        String name = "Tester";

        // when
        Member member = Member.createMember(
                nickname, password, email, name, "010-1234-5678",
                "Address", "2000-01-01", "image_url", false
        );

        // then
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getPassword()).isEqualTo(password);
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getRole()).isEqualTo(MemberRole.USER);
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("회원 생성 실패 - 유효하지 않은 닉네임")
    void createMember_fail_invalidNickname() {
        assertThatThrownBy(() -> Member.createMember(
                "", "password", "test@example.com", "name",
                "010-1234-5678", "Address", "2000-01-01", "image_url", false
        )).isInstanceOf(MemberDomainException.class)
                .hasMessage(MemberErrorCode.INVALID_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("회원 생성 실패 - 유효하지 않은 비밀번호")
    void createMember_fail_invalidPassword() {
        assertThatThrownBy(() -> Member.createMember(
                "nickname", "", "test@example.com", "name",
                "010-1234-5678", "Address", "2000-01-01", "image_url", false
        )).isInstanceOf(MemberDomainException.class)
                .hasMessage(MemberErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("회원 생성 실패 - 유효하지 않은 이메일")
    void createMember_fail_invalidEmail() {
        assertThatThrownBy(() -> Member.createMember(
                "nickname", "password", null, "name",
                "010-1234-5678", "Address", "2000-01-01", "image_url", false
        )).isInstanceOf(MemberDomainException.class)
                .hasMessage(MemberErrorCode.INVALID_EMAIL.getMessage());
    }
}
