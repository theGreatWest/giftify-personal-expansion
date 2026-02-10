package com.giftify.seller.core.domain;

public enum SellerStatus {
    PENDING("승인대기"),
    ACTIVE("승인완료"),
    SUSPENDED("정지"),
    REJECTED("반려");

    private final String status;

    SellerStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public boolean canApprove() {
        return this == PENDING;
    }

    public boolean canReject() {
        return this == PENDING;
    }

    public boolean canSuspend() {
        return this == ACTIVE;
    }

    public boolean canReleaseSuspension() {
        return this == SUSPENDED;
    }

    public boolean canReapply() {
        return this == REJECTED;
    }
}