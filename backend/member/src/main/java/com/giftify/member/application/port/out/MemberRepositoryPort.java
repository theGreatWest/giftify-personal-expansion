package com.giftify.member.application.port.out;

import com.giftify.member.core.domain.Member;

import java.util.Optional;

public interface MemberRepositoryPort {
    Member save(Member member);

    void deleteById(Long id);
    void deleteByEmail(String email);

    Optional<Member> findById(Long id);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByPhone(String phone);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}