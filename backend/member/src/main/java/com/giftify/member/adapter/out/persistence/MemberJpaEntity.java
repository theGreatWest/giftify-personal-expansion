package com.giftify.member.adapter.out.persistence;

import com.giftify.common.domain.BaseEntity;
import com.giftify.member.core.domain.MemberRole;
import com.giftify.member.core.domain.MemberStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String birthDate;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String name;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Column(nullable = false)
    private boolean isSeller;

    @Builder
    public MemberJpaEntity(Long id, String nickname, String password, String email, String name, String phone, String address, String birthDate, String profileImage, MemberRole role, MemberStatus status, boolean isSeller) {
        super.setId(id);
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.role = role;
        this.status = status;
        this.isSeller = isSeller;
    }
}