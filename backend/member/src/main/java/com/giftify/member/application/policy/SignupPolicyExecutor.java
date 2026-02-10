package com.giftify.member.application.policy;

import com.giftify.member.core.policy.SignupPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SignupPolicyExecutor {
    private final List<SignupPolicy> policies;

    public void executePolicies(SignupContext signupContext) {
        for(SignupPolicy policy : policies) {
            policy.validate(signupContext);
        }
    }
}
