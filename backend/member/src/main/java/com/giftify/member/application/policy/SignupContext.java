package com.giftify.member.application.policy;

import com.giftify.member.application.port.in.RegisterMemberCommand;

public record SignupContext(
        String nickname,
        String password,
        String email,
        String phone
) {
    public static SignupContext from(RegisterMemberCommand command) {
        return new SignupContext(
                command.nickname(),
                command.password(),
                command.email(),
                command.phone()
        );
    }
}
