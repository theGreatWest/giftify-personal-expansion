package com.giftify.member.adapter.out.persistence;

import com.giftify.member.core.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberJpaEntity toEntity(Member domain) {
        return MemberJpaEntity.builder()
                .id(domain.getId())
                .nickname(domain.getNickname())
                .password(domain.getPassword())
                .email(domain.getEmail())
                .name(domain.getName())
                .phone(domain.getPhone())
                .address(domain.getAddress())
                .birthDate(domain.getBirthDate())
                .profileImage(domain.getProfileImage())
                .role(domain.getRole())
                .status(domain.getStatus())
                .isSeller(domain.isSeller())
                .build();
    }

    public Member toDomain(MemberJpaEntity entity) {
        return Member.reconstruct(
                entity.getId(),
                entity.getNickname(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getName(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getBirthDate(),
                entity.getProfileImage(),
                entity.getRole(),
                entity.getStatus(),
                entity.isSeller(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}