package com.giftify.seller.core.domain;

public enum BankCode {
    KB("국민은행"),
    SHINHAN("신한은행"),
	WOORI("우리은행"),
	HANA("하나은행"),
	NH("농협은행"),
	IBK("기업은행"),
	SC("SC제일은행"),
	CITI("씨티은행"),

    BUSAN("부산은행"),
	DAEGU("대구은행"),
	GWANGJU("광주은행"),
	JEONBUK("전북은행"),
	GYEONGNAM("경남은행"),
	JEJU("제주은행"),

    KDB("산업은행"),
    EXIM("수출입은행"),
    SUHYUP("수협은행"),

    KAKAO("카카오뱅크"),
    TOSS("토스뱅크"),
    K_BANK("케이뱅크"),

    MIRAE("미래에셋증권"),
    SAMSUNG("삼성증권"),
    NH_INVEST("NH투자증권"),
    KB_INVEST("KB증권");

    private final String bank;

    BankCode(String bank) {
        this.bank = bank;
    }

    public String getBank() {
        return bank;
    }
}
