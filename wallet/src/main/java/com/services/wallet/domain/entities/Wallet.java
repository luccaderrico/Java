package com.services.wallet.domain.entities;

import com.services.wallet.domain.entities.enums.Currency;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Wallet {
    private Client client;
    private Currency currency;
    private BigDecimal balance;
    public Wallet(Client client) {
        this(client, Currency.BRL, BigDecimal.ZERO);
    }
}
