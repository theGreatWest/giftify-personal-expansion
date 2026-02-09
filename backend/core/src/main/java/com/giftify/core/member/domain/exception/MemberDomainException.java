package com.giftify.core.member.domain.exception;

import com.giftify.common.exception.DomainException;

public class MemberDomainException extends DomainException {

    public MemberDomainException(MemberErrorCode errorCode) {
        super(errorCode);
    }
}
