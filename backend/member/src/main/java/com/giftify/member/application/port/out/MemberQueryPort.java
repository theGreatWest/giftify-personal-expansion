package com.giftify.member.application.port.out;

public interface MemberQueryPort {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
