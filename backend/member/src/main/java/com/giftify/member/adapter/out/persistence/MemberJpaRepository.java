package com.giftify.member.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    Optional<MemberJpaEntity> findByEmail(String email);

    Optional<MemberJpaEntity> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}