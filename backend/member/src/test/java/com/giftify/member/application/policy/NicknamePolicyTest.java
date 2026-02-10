package com.giftify.member.application.policy;

import com.giftify.member.application.port.out.MemberRepositoryPort;
import com.giftify.member.core.exception.MemberBusinessException;
import com.giftify.member.core.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NicknamePolicyTest {

    @Mock
    private MemberRepositoryPort memberRepositoryPort;

    @InjectMocks
    private NicknamePolicy nicknamePolicy;

    @Test
    @DisplayName("닉네임 중복 검증 성공 - 사용 가능한 닉네임")
    void validate_success() {
        // given
        SignupContext context = new SignupContext("newNickname", "Password@123", "test@example.com", "010-1234-5678");
        given(memberRepositoryPort.existsByNickname("newNickname")).willReturn(false);

        // when & then
        assertThatNoException().isThrownBy(() -> nicknamePolicy.validate(context));
    }

    @Test
    @DisplayName("닉네임 중복 검증 실패 - 이미 존재하는 닉네임")
    void validate_fail_duplicateNickname() {
        // given
        SignupContext context = new SignupContext("duplicate", "Password@123", "test@example.com", "010-1234-5678");
        given(memberRepositoryPort.existsByNickname("duplicate")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> nicknamePolicy.validate(context))
                .isInstanceOf(MemberBusinessException.class)
                .hasMessage(MemberErrorCode.DUPLICATED_NICKNAME.getMessage());
    }
}
