package com.services.wallet.domain.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ResourceException extends RuntimeException {
    String message;
    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    @Nullable
    BusinessErrorType errorType;

    @Nullable
    Throwable cause;

    public ResourceException(String message) {
        this.message = message;
    }
    public ResourceException(String message, HttpStatus httpStatus, BusinessErrorType errorType) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.errorType = errorType;
    }
}
