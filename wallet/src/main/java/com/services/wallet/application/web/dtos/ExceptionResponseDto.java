package com.services.wallet.application.web.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.services.wallet.application.web.exceptions.ErrorType;

@JsonNaming(SnakeCaseStrategy.class)
public record ExceptionResponseDto(
        ErrorType error,
        String message
) {
}
