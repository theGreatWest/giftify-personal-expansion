package com.giftify.member.core.policy;

import com.giftify.member.application.policy.SignupContext;

public interface SignupPolicy {
    void validate(SignupContext context);
}
