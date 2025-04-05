package com.services.wallet.domain.entities;

import com.services.wallet.domain.entities.enums.OperationType;

import java.math.*;
import java.time.*;

public record Transaction(
        Wallet wallet,
        BigDecimal amount,
        OperationType type,

        LocalDateTime transactionDate
) {
    public Transaction(Wallet wallet, BigDecimal amount, OperationType operationType) {
        this(wallet, amount, operationType, LocalDateTime.now());
    }

    public Transaction(Wallet wallet, BigDecimal amount, OperationType type, LocalDateTime transactionDate) {
        this.wallet = wallet;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate != null ? transactionDate : LocalDateTime.now(); // Use provided date or current time
    }
}
