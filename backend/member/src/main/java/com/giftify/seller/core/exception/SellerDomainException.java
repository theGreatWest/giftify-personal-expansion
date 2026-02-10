package com.giftify.seller.core.exception;

import com.giftify.common.exception.DomainException;

public class SellerDomainException extends DomainException {

    public SellerDomainException(SellerErrorCode errorCode) {
        super(errorCode);
    }
}
