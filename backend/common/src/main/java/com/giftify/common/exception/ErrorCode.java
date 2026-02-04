package com.giftify.common.exception;

public enum ErrorCode {

    INVALID_INPUT("C001", "입력값이 올바르지 않습니다"),
    DOMAIN_RULE_VIOLATION("C002", "도메인 규칙을 위반했습니다"),
    INTERNAL_SERVER_ERROR("C003", "서버 내부 에러가 발생했습니다"),
    RESOURCE_NOT_FOUND("C004", "리소스를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED("C005", "지원하지 않는 HTTP 메서드입니다"),
    NOT_NULL("C006", "값이 null이어서는 안됩니다");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
