package com.giftify.seller.core.domain;

public enum SellerType {
    INDIVIDUAL("크리에이터"),
    BUSINESS("브랜드");

    private final String type;

    SellerType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isBusiness() {
        return this == BUSINESS;
    }
}