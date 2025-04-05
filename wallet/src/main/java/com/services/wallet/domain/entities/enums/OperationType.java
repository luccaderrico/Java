package com.services.wallet.domain.entities.enums;

public enum OperationType {
    DEBIT, CREDIT;

    public static OperationType toOperationType(String currency) {
        return OperationType.valueOf(currency);
    }
}
