package com.services.wallet.application.web.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.services.wallet.application.web.annotations.CPF_CNPJ;
import com.services.wallet.domain.entities.DebitRequest;
import com.services.wallet.application.web.utils.DocumentNumberValidator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonNaming(SnakeCaseStrategy.class)
public record DebitRequestDto(
        @CPF_CNPJ
        @NotNull(message = "document_number is required")
        String documentNumber,
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "Invalid amount")
        BigDecimal amount
) {
    public static DebitRequest toDebitRequest(DebitRequestDto debitRequestDto) {
        return new DebitRequest(
            DocumentNumberValidator.formatDocumentNumber(debitRequestDto.documentNumber),
            debitRequestDto.amount
        );
    }
}
