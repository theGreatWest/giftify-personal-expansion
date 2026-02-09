package com.giftify.member.application.service;

import com.giftify.member.application.port.in.MemberUseCase;
import com.giftify.member.core.domain.Member;
import com.giftify.member.core.exception.MemberDomainException;
import com.giftify.member.core.exception.MemberErrorCode;
import com.giftify.member.application.port.in.RegisterMemberCommand;
import com.giftify.member.application.port.out.MemberRepositoryPort;
import com.giftify.member.application.port.out.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final PasswordEncoderPort passwordEncoderPort;
    private final MemberRepositoryPort memberRepositoryPort;

    @Override
    @Transactional
    public Long registerMember(RegisterMemberCommand command) {
        // 이메일 중복 확인
        if (memberRepositoryPort.existsByEmail(command.email())) {
            log.debug("이미 존재하는 이메일입니다. email={}", command.email());
            throw new MemberDomainException(MemberErrorCode.DUPLICATED_EMAIL);
        }

        // 닉네임 중복 확인
        if (memberRepositoryPort.existsByNickname(command.nickname())) {
            log.debug("이미 존재하는 닉네임입니다. nickname={}", command.nickname());
            throw new MemberDomainException(MemberErrorCode.DUPLICATED_NICKNAME);
        }

        String encodedPassword = passwordEncoderPort.encode(command.password());

        Member member = Member.createMember(
                command.nickname(),
                encodedPassword,
                command.email(),
                command.name(),
                command.phone(),
                command.address(),
                command.birthDate(),
                command.profileImage(),
                command.isSeller()
        );

        Member savedMember = memberRepositoryPort.save(member);
        return savedMember.getId();
    }

    @Override
    public Member findById(Long id) {
        return memberRepositoryPort.findById(id)
                .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}