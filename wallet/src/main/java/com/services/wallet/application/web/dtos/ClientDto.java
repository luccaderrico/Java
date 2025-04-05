package com.services.wallet.application.web.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.services.wallet.application.web.annotations.CPF_CNPJ;
import com.services.wallet.domain.entities.Client;
import com.services.wallet.application.web.utils.DocumentNumberValidator;
import com.services.wallet.domain.entities.enums.PersonType;
import jakarta.validation.constraints.*;

@JsonNaming(SnakeCaseStrategy.class)
public record ClientDto(
        @CPF_CNPJ
        @NotNull(message = "document_number is required")
        String documentNumber,
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "person_type is required")
        String personType
) {
    public static Client toClient(ClientDto clientDto) {
        return new Client(
            DocumentNumberValidator.formatDocumentNumber(clientDto.documentNumber),
            clientDto.name,
            PersonType.toPersonType(clientDto.personType)
        );
    }

    public static ClientDto toClientDto(Client client) {
        return new ClientDto(
            client.documentNumber(),
            client.name(),
            PersonType.fromPersonType(client.personType())
        );
    }
}
