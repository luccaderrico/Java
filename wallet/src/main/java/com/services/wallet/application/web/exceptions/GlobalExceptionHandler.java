package com.services.wallet.application.web.exceptions;

import com.services.wallet.application.web.dtos.ExceptionResponseDto;
import com.services.wallet.domain.exceptions.BusinessErrorType;
import com.services.wallet.domain.exceptions.BusinessException;
import com.services.wallet.domain.exceptions.ResourceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.invoke.MissingParametersException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.services.wallet.application.web.exceptions.ErrorType.NOT_FOUND;
import static com.services.wallet.application.web.exceptions.ErrorType.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ExceptionResponseDto> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exc) {
        log.error("Method Argument Not Valid: {}", exc.getMessage());

        List<String> errors = exc.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.status(BAD_REQUEST).body(
            new ExceptionResponseDto(
                ErrorType.BAD_REQUEST,
                String.join("; ", errors)
            )
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    ResponseEntity<ExceptionResponseDto> missingRequestHeaderExceptionHandler(MissingRequestHeaderException exc) {
        log.error("Missing request header: {}", exc.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(
            new ExceptionResponseDto(
                ErrorType.BAD_REQUEST,
                exc.getMessage()
            )
        );
    }
    @ExceptionHandler(
        value = {
            IllegalArgumentException.class,
            MissingParametersException.class,
            BusinessException.class
        }
    )
    ResponseEntity<ExceptionResponseDto> illegalArgumentExceptionHandler(Exception exc) {
        log.error("Illegal argument: {}", exc.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(
            new ExceptionResponseDto(
                ErrorType.BAD_REQUEST,
                exc.getMessage()
            )
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ExceptionResponseDto> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException exc) {
        log.error("media type not supported: {}", exc.getMessage());
        return ResponseEntity.status(UNSUPPORTED_MEDIA_TYPE).body(
            new ExceptionResponseDto(
                ErrorType.UNSUPPORTED_MEDIA_TYPE,
                exc.getLocalizedMessage()
            )
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ExceptionResponseDto> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException exc) {
        log.error("Invalid http method: {}", exc.getMessage());
        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(
                new ExceptionResponseDto(
                    UNSUPPORTED_METHOD,
                    exc.getLocalizedMessage()
                )
        );
    }

    @ExceptionHandler(ResourceException.class)
    ResponseEntity<ExceptionResponseDto> resourceExceptionHandler(ResourceException exc) {
        log.error("Resource exception: {}", exc.getMessage());

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                new ExceptionResponseDto(
                        exc.getErrorType().equals(BusinessErrorType.NOT_FOUND) ? NOT_FOUND : INTERNAL_ERROR,
                        exc.getLocalizedMessage()
                )
        );
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    ResponseEntity<ExceptionResponseDto> exceptionHandler(Exception exc) {
        log.error("unhandled exception: {}", exc.getMessage());

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
            new ExceptionResponseDto(
                INTERNAL_ERROR,
                exc.getMessage()
            )
        );
    }
}
