package com.services.wallet.domain.exceptions.business;

import com.services.wallet.domain.exceptions.BusinessErrorType;
import com.services.wallet.domain.exceptions.BusinessException;

public class InsufficientFundsException extends BusinessException {
    public static final String insufficientFundsExceptionMessage = "Insufficient funds";

    public InsufficientFundsException(String message, BusinessErrorType businessErrorType){
        super(message, businessErrorType);
    }
}
