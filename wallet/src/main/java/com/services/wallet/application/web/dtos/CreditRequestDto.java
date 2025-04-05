package com.services.wallet.application.web.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.services.wallet.application.web.annotations.CPF_CNPJ;
import com.services.wallet.domain.entities.CreditRequest;
import com.services.wallet.application.web.utils.DocumentNumberValidator;
import jakarta.validation.constraints.*;

import java.math.*;

@JsonNaming(SnakeCaseStrategy.class)
public record CreditRequestDto(
        @CPF_CNPJ
        @NotNull(message = "document_number is required")
        String documentNumber,
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "Invalid amount")
        BigDecimal amount
) {
    public static CreditRequest toCreditRequest(CreditRequestDto creditRequestDto) {
        return new CreditRequest(
                DocumentNumberValidator.formatDocumentNumber(creditRequestDto.documentNumber),
                creditRequestDto.amount
        );
    }
}
