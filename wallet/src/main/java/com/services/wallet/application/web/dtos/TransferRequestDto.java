package com.services.wallet.application.web.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.services.wallet.application.web.annotations.CPF_CNPJ;
import com.services.wallet.domain.entities.TransferRequest;
import com.services.wallet.application.web.utils.DocumentNumberValidator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonNaming(SnakeCaseStrategy.class)
public record TransferRequestDto(

        @CPF_CNPJ
        @NotNull(message = "sender_document_number is required")
        String senderDocumentNumber,

        @CPF_CNPJ
        @NotNull(message = "recipient_document_number is required")
        String recipientDocumentNumber,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "Invalid amount")
        BigDecimal amount
) {

    public static TransferRequest toTransferRequest(TransferRequestDto transferRequestDto) {
        return new TransferRequest(
            DocumentNumberValidator.formatDocumentNumber(transferRequestDto.senderDocumentNumber),
            DocumentNumberValidator.formatDocumentNumber(transferRequestDto.recipientDocumentNumber),
            transferRequestDto.amount
        );
    }
}
