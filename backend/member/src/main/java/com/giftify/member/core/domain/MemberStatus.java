package com.giftify.member.core.domain;

public enum MemberStatus {
    ACTIVE("정상", "활동 가능한 계정입니다."),
    SUSPENDED("정지", "규칙 위반으로 정지된 계정입니다."),
    DORMANT("휴면", "1년간 활동하지 않아 휴면 처리된 계정입니다."),
    WITHDRAWN("탈퇴", "탈퇴한 회원으로 탈퇴 처리 후 3개월간 정보를 보관합니다.");

    private final String status;
    private final String description;

    MemberStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    // 로그인 가능 여부 체크
    // 휴면, 탈퇴 회원의 경우 본인 인증 후 정상 상태로 바뀌어야 로그인 가능
    public boolean canLogin() {
        return this == ACTIVE;
    }

    // 상태 전이 가능 여부 체크
    public boolean canTransitionTo(MemberStatus targetStatus) {
        return switch (this){
            case ACTIVE ->
                    targetStatus == SUSPENDED
                    || targetStatus == DORMANT
                    || targetStatus == WITHDRAWN;
            case SUSPENDED, DORMANT, WITHDRAWN ->
                    targetStatus == ACTIVE;
        };
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isSuspended() {
        return this == SUSPENDED;
    }

    public boolean isDormant() {
        // 본인인증 후 로그인 가능
        return this == DORMANT;
    }

    public boolean isWithdrawn() {
        // 본인인증 후 로그인 가능
        return this == WITHDRAWN;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
