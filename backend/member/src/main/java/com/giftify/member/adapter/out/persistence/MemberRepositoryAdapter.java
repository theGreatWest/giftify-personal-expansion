package com.giftify.member.adapter.out.persistence;

import com.giftify.member.core.domain.Member;
import com.giftify.member.application.port.out.MemberRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepositoryPort {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberMapper memberMapper;

    @Override
    public Member save(Member member) {
        MemberJpaEntity entity = memberMapper.toEntity(member);
        MemberJpaEntity saved = memberJpaRepository.save(entity);
        return memberMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        memberJpaRepository.deleteById(id);
    }

    @Override
    public void deleteByEmail(String email) {
        memberJpaRepository.findByEmail(email)
                .ifPresent(memberJpaRepository::delete);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id)
                .map(memberMapper::toDomain);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberJpaRepository.findByEmail(email)
                .map(memberMapper::toDomain);
    }

    @Override
    public Optional<Member> findByPhone(String phone) {
        return memberJpaRepository.findByPhone(phone)
                .map(memberMapper::toDomain);
    }
}