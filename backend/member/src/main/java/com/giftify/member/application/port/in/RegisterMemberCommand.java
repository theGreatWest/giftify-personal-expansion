package com.giftify.member.application.port.in;

public record RegisterMemberCommand(
        String nickname,
        String password,
        String email,
        String name,
        String phone,
        String address,
        String birthDate,
        String profileImage,
        boolean isSeller
) {}