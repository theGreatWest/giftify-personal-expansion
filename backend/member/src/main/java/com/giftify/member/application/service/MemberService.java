package com.giftify.member.application.service;

import com.giftify.member.application.policy.SignupContext;
import com.giftify.member.application.policy.SignupPolicyExecutor;
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

    private final SignupPolicyExecutor signupPolicyExecutor;

    @Override
    @Transactional
    public Long registerMember(RegisterMemberCommand command) {
        // signupContext로 변환 후 policy정책 위임
        SignupContext signupContext = SignupContext.from(command);

        // 회원가입 정책 검증
        // ✅ 비밀번호 조건 충족 여부
        // ✅ 닉네임 중복 검사
        // ▶️ 이메일 인증 여부, 이메일 중복 검사
        // 4. 휴대폰 번호 인증 여부
        signupPolicyExecutor.executePolicies(signupContext);

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