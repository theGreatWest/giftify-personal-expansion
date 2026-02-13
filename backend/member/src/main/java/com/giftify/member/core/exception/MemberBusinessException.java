package com.giftify.member.core.exception;

import com.giftify.common.exception.BusinessException;

public class MemberBusinessException extends BusinessException {

    public MemberBusinessException(MemberErrorCode errorCode) {
        super(errorCode);
    }
}
