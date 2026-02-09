package com.giftify.member.adapter.in.web.dto.request;

import com.giftify.member.application.port.in.RegisterMemberCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterMemberRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2~16자여야 합니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 8~16자여야 합니다.")
        String password,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        String email,

        String name,
        String phone,
        String address,
        String birthDate,
        String profileImage,
        boolean isSeller
) {
    public RegisterMemberCommand toCommand() {
        return new RegisterMemberCommand(
                nickname, password, email, name, phone, address, birthDate, profileImage, isSeller
        );
    }
}