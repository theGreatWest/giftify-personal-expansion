package com.giftify.member.api.dto;

import com.giftify.member.core.domain.Member;
import com.giftify.member.core.domain.MemberRole;
import com.giftify.member.core.domain.MemberStatus;

public record MemberSummary(
        Long id,
        String email,
        String nickname,
        String address,
        String phone,
        MemberStatus status,
        MemberRole role,
        boolean isSeller
) {
    public static MemberSummary of(Member member) {
        return new MemberSummary(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getAddress(),
                member.getPhone(),
                member.getStatus(),
                member.getRole(),
                member.isSeller()
        );
    }
}
