package com.services.wallet.domain.entities;

import com.services.wallet.domain.exceptions.business.InsufficientFundsException;
import com.services.wallet.domain.exceptions.business.InvalidAmountException;
import com.services.wallet.domain.exceptions.BusinessErrorType;
import lombok.extern.slf4j.*;

import java.math.*;

import static java.math.BigDecimal.*;

@Slf4j
public record DebitRequest(
        String documentNumber,
        BigDecimal amount
) {

    public void isValid(BigDecimal walletBalance) {
        log.info("Validating debit");
        if (amount.equals(ZERO)) {
            throw new InvalidAmountException(InvalidAmountException.invalidAmountExceptionMessage, BusinessErrorType.INVALID_ATTRIBUTE);
        } else if (this.amount.compareTo(walletBalance) >= 0) {
            throw new InsufficientFundsException(InsufficientFundsException.insufficientFundsExceptionMessage, BusinessErrorType.INSUFFICIENT_BALANCE);
        } else {
            log.info("Debit successfully validated");
        }
    }
}
