package com.giftify.seller.core.exception;

import com.giftify.common.exception.ErrorCode;

public enum SellerErrorCode implements ErrorCode {

    // 생성 / 기본 정보
    INVALID_MEMBER_ID("S001", "회원 ID는 필수 값입니다."),
    INVALID_SELLER_TYPE("S002", "판매자 유형은 필수 값입니다."),
    INVALID_SELLER_NAME("S003", "판매자명은 필수이며 공백일 수 없습니다."),

    // 상태 전이
    INVALID_STATUS_TRANSITION("S010", "허용되지 않은 판매자 상태 전이입니다."),
    SELLER_NOT_ACTIVE("S011", "활성화되지 않은 판매자입니다."),
    SELLER_NOT_PENDING("S012", "승인 대기 상태의 판매자가 아닙니다."),
    SELLER_ALREADY_APPROVED("S013", "이미 승인된 판매자입니다."),

    // 승인 / 정책
    BUSINESS_INFO_REQUIRED("S021", "사업자 판매자는 사업자 정보가 필수입니다."),

    // 사업자 정보
    INVALID_BUSINESS_INFO("S030", "유효하지 않은 사업자 정보입니다."),
    BUSINESS_INFO_NOT_ALLOWED("S031", "개인 판매자는 사업자 정보를 등록할 수 없습니다."),

    // 정산 정보
    INVALID_SETTLEMENT_INFO("S040", "유효하지 않은 정산 정보입니다."),
    SETTLEMENT_INFO_REQUIRED("S041", "정산 정보는 필수입니다."),

    // 조회 / 존재 여부
    SELLER_NOT_FOUND("S050", "판매자를 찾을 수 없습니다."),
    DUPLICATED_SELLER("S051", "이미 판매자로 등록된 회원입니다.");

    private final String code;
    private final String message;

    SellerErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
