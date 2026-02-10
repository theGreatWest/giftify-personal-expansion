package com.giftify.member.application.policy;

import com.giftify.member.application.port.in.MemberUseCase;
import com.giftify.member.core.exception.MemberBusinessException;
import com.giftify.member.core.exception.MemberErrorCode;
import com.giftify.member.core.policy.SignupPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NicknamePolicy implements SignupPolicy {

    private final MemberUseCase memberUseCase;

    @Override
    public void validate(SignupContext context) {
        String nickname = context.nickname();

        if(memberUseCase.existsByNickname(nickname)){
            throw new MemberBusinessException(MemberErrorCode.DUPLICATED_NICKNAME);
        }
    }
}
