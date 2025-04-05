package com.services.wallet.domain.exceptions;

import lombok.*;
import org.springframework.http.*;
import org.springframework.lang.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BusinessException extends RuntimeException {
    String message;
    HttpStatus httpStatus;

    @Nullable
    BusinessErrorType businessErrorType;

    @Nullable
    Throwable cause;

    public BusinessException(String message, BusinessErrorType businessErrorType) {
        this.message = message;
        this.businessErrorType = businessErrorType;
    }
}
