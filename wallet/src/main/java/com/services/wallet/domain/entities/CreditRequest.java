package com.services.wallet.domain.entities;

import com.services.wallet.domain.exceptions.business.InvalidAmountException;
import com.services.wallet.domain.exceptions.BusinessErrorType;
import lombok.extern.slf4j.*;

import java.math.*;

import static java.math.BigDecimal.*;

@Slf4j
public record CreditRequest(
        String documentNumber,
        BigDecimal amount
) {
    public void isValid() {
        log.info("Validating credit");
        if (this.amount.equals(ZERO)) {
            throw new InvalidAmountException(InvalidAmountException.invalidAmountExceptionMessage, BusinessErrorType.INVALID_ATTRIBUTE);
        } else {
            log.info("Credit successfully validated");
        }
    }
}
