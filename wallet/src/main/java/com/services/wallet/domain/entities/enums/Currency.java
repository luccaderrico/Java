package com.services.wallet.domain.entities.enums;

public enum Currency {
    BRL,
    NOT_MAPPED;

    public static Currency toCurrency(String currency) {
        try {
            return Currency.valueOf(currency);
        } catch (IllegalArgumentException exc) {
            return NOT_MAPPED;
        }
    }
}
