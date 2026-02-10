package com.giftify.member.application.port.in;

import com.giftify.member.core.domain.Member;

public interface MemberUseCase {
    Long registerMember(RegisterMemberCommand command);
    Member findById(Long id);
    Member findByEmail(String email);
    boolean existsByNickname(String nickname);
}
