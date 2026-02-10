package com.giftify.member.application.policy;

import com.giftify.member.application.port.in.MemberUseCase;
import com.giftify.member.application.port.out.MemberRepositoryPort;
import com.giftify.member.core.exception.MemberBusinessException;
import com.giftify.member.core.exception.MemberErrorCode;
import com.giftify.member.core.policy.SignupPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NicknamePolicy implements SignupPolicy {

    private final MemberRepositoryPort memberRepositoryPort;

    @Override
    public void validate(SignupContext context) {
        String nickname = context.nickname();

        if(memberRepositoryPort.existsByNickname(nickname)){
            throw new MemberBusinessException(MemberErrorCode.DUPLICATED_NICKNAME);
        }
    }
}
