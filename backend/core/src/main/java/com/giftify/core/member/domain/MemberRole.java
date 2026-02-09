package com.giftify.core.member.domain;

public enum MemberRole {
    USER("일반 회원"),
    ADMIN("관리자");

    private final String description;

    MemberRole(String description) {
        this.description = description;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isUser() {
        return this == USER;
    }

    public String getDescription() {
        return description;
    }
}
