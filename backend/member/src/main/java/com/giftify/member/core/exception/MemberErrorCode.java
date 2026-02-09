package com.giftify.member.core.exception;

import com.giftify.common.exception.ErrorCode;

public enum MemberErrorCode implements ErrorCode {

    INVALID_NICKNAME("M001", "닉네임은 필수이며 공백일 수 없습니다."),
    INVALID_PASSWORD("M002", "비밀번호는 필수 값입니다."),
    INVALID_EMAIL("M003", "이메일은 필수 값입니다."),
    INVALID_STATUS_TRANSITION("M004", "허용되지 않은 회원 상태 전이입니다."),
    MEMBER_NOT_ACTIVE("M005", "활성화되지 않은 회원입니다."),
    CANNOT_CHANGE_PASSWORD("M006", "비밀번호 변경이 불가능한 상태입니다."),
    CANNOT_LOGIN("M007", "로그인 불가능한 상태입니다."),
    DUPLICATED_EMAIL("M008", "이미 존재하는 이메일입니다."),
    DUPLICATED_NICKNAME("M009", "이미 존재하는 닉네임입니다."),
    MEMBER_NOT_FOUND("M010", "회원을 찾을 수 없습니다.");

    private final String code;
    private final String message;

    MemberErrorCode(String code, String message) {
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
