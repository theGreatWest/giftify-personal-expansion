package com.giftify.member.adapter.out.persistence;

import com.giftify.member.application.port.out.MemberQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryAdapter implements MemberQueryPort {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public boolean existsByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return memberJpaRepository.existsByNickname(nickname);
    }
}
