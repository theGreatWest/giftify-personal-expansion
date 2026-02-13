package com.giftify.member.application.port.in;

public interface MemberQueryUseCase {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
