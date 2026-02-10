package com.giftify.member.application.policy;

import com.giftify.member.core.exception.MemberBusinessException;
import com.giftify.member.core.exception.MemberErrorCode;
import com.giftify.member.core.policy.SignupPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignupPolicyExecutorTest {

    @Test
    @DisplayName("모든 정책을 순차적으로 실행한다")
    void executePolicies_success() {
        // given
        SignupPolicy passingPolicy1 = context -> {};
        SignupPolicy passingPolicy2 = context -> {};
        SignupPolicyExecutor executor = new SignupPolicyExecutor(List.of(passingPolicy1, passingPolicy2));
        SignupContext context = new SignupContext("tester", "Password@123", "test@example.com", "010-1234-5678");

        // when & then
        assertThatNoException().isThrownBy(() -> executor.executePolicies(context));
    }

    @Test
    @DisplayName("정책 검증 실패 시 즉시 예외를 던진다")
    void executePolicies_fail_stopOnFirstViolation() {
        // given
        SignupPolicy failingPolicy = context -> {
            throw new MemberBusinessException(MemberErrorCode.WEAK_PASSWORD);
        };
        SignupPolicy passingPolicy = context -> {};
        SignupPolicyExecutor executor = new SignupPolicyExecutor(List.of(failingPolicy, passingPolicy));
        SignupContext context = new SignupContext("tester", "weak", "test@example.com", "010-1234-5678");

        // when & then
        assertThatThrownBy(() -> executor.executePolicies(context))
                .isInstanceOf(MemberBusinessException.class)
                .hasMessage(MemberErrorCode.WEAK_PASSWORD.getMessage());
    }
}
