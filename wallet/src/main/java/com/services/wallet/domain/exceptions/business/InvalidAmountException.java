package com.services.wallet.domain.exceptions.business;

import com.services.wallet.domain.exceptions.BusinessErrorType;
import com.services.wallet.domain.exceptions.BusinessException;

public class InvalidAmountException extends BusinessException {

    public static final String invalidAmountExceptionMessage = "Invalid amount";
    public InvalidAmountException(String message, BusinessErrorType businessErrorType) {
        super(message, businessErrorType);
    }
}
