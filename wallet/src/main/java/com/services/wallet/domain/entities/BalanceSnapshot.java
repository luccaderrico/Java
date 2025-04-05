package com.services.wallet.domain.entities;

import com.services.wallet.domain.entities.enums.OperationType;

import java.math.*;
import java.time.*;
import java.util.*;

public record BalanceSnapshot(
        String clientDocumentNumber, BigDecimal balance,
        LocalDateTime snapshotDate
) {
    public BalanceSnapshot calculateNewSnapshot(
            List<Transaction> transactions,
            LocalDateTime inputDate
    ) {
        BigDecimal newBalance = transactions.stream()
                .map(transaction -> transaction.type() == OperationType.CREDIT
                    ? transaction.amount()
                    : transaction.amount().negate())
                .reduce(this.balance, BigDecimal::add);

        return new BalanceSnapshot(this.clientDocumentNumber, newBalance, inputDate);
    }
}

