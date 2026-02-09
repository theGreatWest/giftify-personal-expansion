package com.giftify.member.adapter.out.persistence;

import com.giftify.member.core.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberRepositoryAdapterTest {

    @Mock
    private MemberJpaRepository memberJpaRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberRepositoryAdapter memberRepositoryAdapter;

    @Test
    @DisplayName("회원 저장 성공")
    void save_success() {
        // given
        Member member = Member.createMember("tester", "password", "test@example.com", "Tester", "010-1234-5678", "Addr", "2000-01-01", null, false);
        MemberJpaEntity entity = MemberJpaEntity.builder().build();
        Member savedMember = Member.reconstruct(1L, "tester", "password", "test@example.com", "Tester", "010-1234-5678", "Addr", "2000-01-01", null, null, null, false, null, null);

        given(memberMapper.toEntity(any(Member.class))).willReturn(entity);
        given(memberJpaRepository.save(any(MemberJpaEntity.class))).willReturn(entity);
        given(memberMapper.toDomain(any(MemberJpaEntity.class))).willReturn(savedMember);

        // when
        Member result = memberRepositoryAdapter.save(member);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        verify(memberJpaRepository).save(any(MemberJpaEntity.class));
    }

    @Test
    @DisplayName("이메일로 회원 조회")
    void findByEmail() {
        // given
        String email = "test@example.com";
        MemberJpaEntity entity = MemberJpaEntity.builder().build();
        Member member = Member.reconstruct(1L, "tester", "password", email, "Tester", "010-1234-5678", "Addr", "2000-01-01", null, null, null, false, null, null);

        given(memberJpaRepository.findByEmail(email)).willReturn(Optional.of(entity));
        given(memberMapper.toDomain(entity)).willReturn(member);

        // when
        Optional<Member> result = memberRepositoryAdapter.findByEmail(email);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void existsByEmail() {
        // given
        String email = "test@example.com";
        given(memberJpaRepository.existsByEmail(email)).willReturn(true);

        // when
        boolean result = memberRepositoryAdapter.existsByEmail(email);

        // then
        assertThat(result).isTrue();
    }
}
