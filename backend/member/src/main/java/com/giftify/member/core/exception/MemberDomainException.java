package com.giftify.member.core.exception;

import com.giftify.common.exception.DomainException;

public class MemberDomainException extends DomainException {

    public MemberDomainException(MemberErrorCode errorCode) {
        super(errorCode);
    }
}
