package com.giftify.member.application.service;

import com.giftify.member.application.port.in.MemberQueryUseCase;
import com.giftify.member.application.port.out.MemberQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQueryService implements MemberQueryUseCase {

    private final MemberQueryPort memberQueryPort;

    @Override
    public boolean existsByEmail(String email) {
        return memberQueryPort.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return memberQueryPort.existsByNickname(nickname);
    }
}
