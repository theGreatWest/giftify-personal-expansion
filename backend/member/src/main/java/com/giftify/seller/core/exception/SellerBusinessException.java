package com.giftify.seller.core.exception;

import com.giftify.common.exception.BusinessException;

public class SellerBusinessException extends BusinessException {

    public SellerBusinessException(SellerErrorCode errorCode) {
        super(errorCode);
    }
}
