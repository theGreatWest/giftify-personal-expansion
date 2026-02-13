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
@Order(2)
public class EmailPolicy implements SignupPolicy {

    private final MemberQueryPort memberQueryPort;

    @Override
    public void validate(SignupContext context) {
        String email = context.email();

        if(memberQueryPort.existsByEmail(email)) {
            throw new MemberBusinessException(MemberErrorCode.DUPLICATED_EMAIL);
        }
    }
}
