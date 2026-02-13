package com.giftify.member.application.policy;

import com.giftify.member.core.exception.MemberBusinessException;
import com.giftify.member.core.exception.MemberErrorCode;
import com.giftify.member.core.policy.SignupPolicy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Order(0)
public class PasswordPolicy implements SignupPolicy {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).+$"
    );

    @Override
    public void validate(SignupContext context) {
        String password = context.password();

        if(!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new MemberBusinessException(MemberErrorCode.WEAK_PASSWORD);
        }
    }
}
