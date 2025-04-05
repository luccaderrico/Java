package com.services.wallet.application.web.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.services.wallet.domain.entities.Balance;

import java.math.*;
import java.time.*;

@JsonNaming(SnakeCaseStrategy.class)
public record BalanceDto(
        BigDecimal amount,
        LocalDateTime date
) {

    public static BalanceDto toBalanceDto(Balance balance) {
        return new BalanceDto(
                balance.amount(),
                balance.date()
        );
    }
}
