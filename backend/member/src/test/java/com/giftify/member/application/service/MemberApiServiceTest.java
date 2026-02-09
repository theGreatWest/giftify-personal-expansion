package com.giftify.member.application.service;

import com.giftify.member.api.dto.MemberSummary;
import com.giftify.member.application.port.in.MemberUseCase;
import com.giftify.member.core.domain.Member;
import com.giftify.member.core.domain.MemberRole;
import com.giftify.member.core.domain.MemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberApiServiceTest {

    @Mock
    private MemberUseCase memberUseCase;

    @InjectMocks
    private MemberApiService memberApiService;

    @Test
    @DisplayName("ID로 회원 조회 시 MemberSummary로 변환되어야 한다")
    void findById_shouldReturnMemberSummary() {
        // given
        Long memberId = 1L;
        Member member = createMember(memberId, "test@example.com", "tester");
        given(memberUseCase.findById(memberId)).willReturn(member);

        // when
        MemberSummary result = memberApiService.findById(memberId);

        // then
        assertThat(result.id()).isEqualTo(member.getId());
        assertThat(result.email()).isEqualTo(member.getEmail());
        assertThat(result.nickname()).isEqualTo(member.getNickname());
        assertThat(result.role()).isEqualTo(member.getRole());
        assertThat(result.status()).isEqualTo(member.getStatus());
        assertThat(result.isSeller()).isEqualTo(member.isSeller());

        verify(memberUseCase).findById(memberId);
    }

    @Test
    @DisplayName("이메일로 회원 조회 시 MemberSummary로 변환되어야 한다")
    void findByEmail_shouldReturnMemberSummary() {
        // given
        String email = "test@example.com";
        Member member = createMember(1L, email, "tester");
        given(memberUseCase.findByEmail(email)).willReturn(member);

        // when
        MemberSummary result = memberApiService.findByEmail(email);

        // then
        assertThat(result.id()).isEqualTo(member.getId());
        assertThat(result.email()).isEqualTo(member.getEmail());
        assertThat(result.nickname()).isEqualTo(member.getNickname());

        verify(memberUseCase).findByEmail(email);
    }

    private Member createMember(Long id, String email, String nickname) {
        return Member.reconstruct(
                id,
                nickname,
                "password",
                email,
                "name",
                "010-1234-5678",
                "address",
                "2000-01-01",
                "image",
                MemberRole.USER,
                MemberStatus.ACTIVE,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
