package com.giftify.member.application.policy;

import com.giftify.member.application.port.out.MemberQueryPort;
import com.giftify.member.core.exception.MemberBusinessException;
import com.giftify.member.core.exception.MemberErrorCode;
import com.giftify.member.core.policy.SignupPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class NicknamePolicy implements SignupPolicy {

    private final MemberQueryPort memberQueryPort;

    @Override
    public void validate(SignupContext context) {
        String nickname = context.nickname();

        if(memberQueryPort.existsByNickname(nickname)){
            throw new MemberBusinessException(MemberErrorCode.DUPLICATED_NICKNAME);
        }
    }
}
