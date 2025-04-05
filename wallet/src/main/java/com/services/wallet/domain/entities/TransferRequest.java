package com.services.wallet.domain.entities;

import java.math.BigDecimal;

public record TransferRequest(
        String sender,
        String recipient,
        BigDecimal amount
) {}
