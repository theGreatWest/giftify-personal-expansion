package com.giftify.member.core.domain;

import com.giftify.common.domain.IdentifiableDomain;
import com.giftify.member.core.exception.MemberDomainException;
import com.giftify.member.core.exception.MemberErrorCode;

import java.time.LocalDateTime;

public class Member extends IdentifiableDomain<Long> {
    String nickname;
    String password;
    String name;
    String email;
    String phone;
    String address;
    String birthDate;
    String profileImage;
    MemberRole role;
    MemberStatus status;
    boolean isSeller;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    // 회원 가입 (신규 — id는 DB에서 자동 생성)
    private Member(
            String nickname,
            String encryptedPassword,
            String email,
            String name,
            String phone,
            String address,
            String birthDate,
            String profileImage,
            boolean isSeller
    ) {
        super();

        validate(nickname != null && !nickname.isBlank(), new MemberDomainException(MemberErrorCode.INVALID_NICKNAME));
        this.nickname = nickname;
        validate(encryptedPassword != null && !encryptedPassword.isBlank(), new MemberDomainException(MemberErrorCode.INVALID_PASSWORD));
        this.password = encryptedPassword;
        validate(email != null && !email.isBlank(), new MemberDomainException(MemberErrorCode.INVALID_EMAIL));
        this.email = email;

        this.name = name;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.isSeller = isSeller;

        this.role = MemberRole.USER;
        this.status = MemberStatus.ACTIVE;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // DB 복원용 (Mapper에서 호출)
    private Member(
            Long id,
            String nickname,
            String encryptedPassword,
            String email,
            String name,
            String phone,
            String address,
            String birthDate,
            String profileImage,
            MemberRole role,
            MemberStatus status,
            boolean isSeller,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        super(id);
        this.nickname = nickname;
        this.password = encryptedPassword;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.role = role;
        this.status = status;
        this.isSeller = isSeller;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 팩토리 메서드: 신규 회원 생성
    public static Member createMember(
            String nickname,
            String encryptedPassword,
            String email,
            String name,
            String phone,
            String address,
            String birthDate,
            String profileImage,
            boolean isSeller
    ) {
        return new Member(nickname, encryptedPassword, email, name, phone, address, birthDate, profileImage, isSeller);
    }

    // 팩토리 메서드: DB에서 복원
    public static Member reconstruct(
            Long id,
            String nickname,
            String encryptedPassword,
            String email,
            String name,
            String phone,
            String address,
            String birthDate,
            String profileImage,
            MemberRole role,
            MemberStatus status,
            boolean isSeller,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Member(id, nickname, encryptedPassword, email, name, phone, address, birthDate, profileImage, role, status, isSeller, createdAt, updatedAt);
    }

    // 비밀번호 변경
    public void changePassword(String newEncryptedPassword) {
        validate(status.canLogin(), new MemberDomainException(MemberErrorCode.CANNOT_CHANGE_PASSWORD));

        validate(newEncryptedPassword != null && !newEncryptedPassword.isBlank(), new MemberDomainException(MemberErrorCode.INVALID_PASSWORD));

        this.password = newEncryptedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    // 상태 변경
    public void changeStatus(MemberStatus newStatus) {
        validate(status.canTransitionTo(newStatus), new MemberDomainException(MemberErrorCode.INVALID_STATUS_TRANSITION));

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    // 로그인 가능 여부
    public void validateLogin(){
        validate(status.canLogin(), new MemberDomainException(MemberErrorCode.CANNOT_LOGIN));
    }

    // getter
    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public MemberRole getRole() {
        return role;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public boolean isSeller() {
        return isSeller;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
