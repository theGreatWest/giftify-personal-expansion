package com.giftify.common.exception;

// 도메인 모델 내부에서 터진 예외
public abstract class DomainException extends BusinessException {

    protected DomainException(ErrorCode errorCode) {
        super(errorCode);
    }
}
