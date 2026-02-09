package com.giftify.member.application.service;

import com.giftify.member.application.port.in.RegisterMemberCommand;
import com.giftify.member.application.port.out.MemberRepositoryPort;
import com.giftify.member.application.port.out.PasswordEncoderPort;
import com.giftify.member.core.domain.Member;
import com.giftify.member.core.exception.MemberDomainException;
import com.giftify.member.core.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private MemberRepositoryPort memberRepositoryPort;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void registerMember_success() {
        // given
        RegisterMemberCommand command = new RegisterMemberCommand(
                "tester", "password123", "test@example.com", "Tester",
                "010-1234-5678", "Address", "2000-01-01", "image", false
        );

        given(memberRepositoryPort.existsByEmail(command.email())).willReturn(false);
        given(memberRepositoryPort.existsByNickname(command.nickname())).willReturn(false);
        given(passwordEncoderPort.encode(command.password())).willReturn("encodedPassword");

        Member member = Member.createMember(
                command.nickname(), "encodedPassword", command.email(), command.name(),
                command.phone(), command.address(), command.birthDate(), command.profileImage(), command.isSeller()
        );
        // Reflection 혹은 reconstruct를 사용하지 않고 id를 주입하기 위해 mock 객체 반환 설정
        // Member는 IdentifiableDomain을 상속받으므로 setId가 있을 수 있음.
        Member savedMember = Member.reconstruct(
                1L, command.nickname(), "encodedPassword", command.email(), command.name(),
                command.phone(), command.address(), command.birthDate(), command.profileImage(),
                null, null, false, null, null
        );
        
        given(memberRepositoryPort.save(any(Member.class))).willReturn(savedMember);

        // when
        Long memberId = memberService.registerMember(command);

        // then
        assertThat(memberId).isEqualTo(1L);
        verify(memberRepositoryPort).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void registerMember_fail_duplicateEmail() {
        // given
        RegisterMemberCommand command = new RegisterMemberCommand(
                "tester", "password123", "duplicate@example.com", "Tester",
                "010-1234-5678", "Address", "2000-01-01", "image", false
        );
        given(memberRepositoryPort.existsByEmail(command.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(command))
                .isInstanceOf(MemberDomainException.class)
                .hasMessage(MemberErrorCode.DUPLICATED_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void registerMember_fail_duplicateNickname() {
        // given
        RegisterMemberCommand command = new RegisterMemberCommand(
                "duplicate", "password123", "test@example.com", "Tester",
                "010-1234-5678", "Address", "2000-01-01", "image", false
        );
        given(memberRepositoryPort.existsByEmail(command.email())).willReturn(false);
        given(memberRepositoryPort.existsByNickname(command.nickname())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(command))
                .isInstanceOf(MemberDomainException.class)
                .hasMessage(MemberErrorCode.DUPLICATED_NICKNAME.getMessage());
    }
}
