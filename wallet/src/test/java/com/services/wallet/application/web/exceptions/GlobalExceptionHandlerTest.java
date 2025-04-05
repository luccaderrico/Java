package com.services.wallet.application.web.exceptions;

import com.services.wallet.application.web.dtos.ExceptionResponseDto;
import com.services.wallet.domain.exceptions.BusinessErrorType;
import com.services.wallet.domain.exceptions.BusinessException;
import com.services.wallet.domain.exceptions.business.InsufficientFundsException;
import com.services.wallet.domain.exceptions.business.InvalidAmountException;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import com.services.wallet.application.web.exceptions.ErrorType;
import com.services.wallet.application.web.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.endpoint.invoke.MissingParametersException;
import org.springframework.boot.actuate.endpoint.invoke.OperationParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.Set;

import static com.services.wallet.application.web.exceptions.ErrorType.BAD_REQUEST;
import static com.services.wallet.domain.exceptions.BusinessErrorType.INSUFFICIENT_BALANCE;
import static com.services.wallet.domain.exceptions.BusinessErrorType.INVALID_ATTRIBUTE;
import static com.services.wallet.domain.exceptions.business.InsufficientFundsException.insufficientFundsExceptionMessage;
import static com.services.wallet.domain.exceptions.business.InvalidAmountException.invalidAmountExceptionMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMethodArgumentNotValidExceptionHandler() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.methodArgumentNotValidExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testMissingRequestHeaderExceptionHandler() {
        MissingRequestHeaderException exception = mock(MissingRequestHeaderException.class);

        when(exception.getMessage()).thenReturn("Authorization is missing");

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.missingRequestHeaderExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHttpMediaTypeNotSupportedExceptionHandler() {
        HttpMediaTypeNotSupportedException exception = new HttpMediaTypeNotSupportedException("Unsupported type");

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.httpMediaTypeNotSupportedExceptionHandler(exception);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }

    @Test
    void testHttpRequestMethodNotSupportedExceptionHandler() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("POST");

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.httpRequestMethodNotSupportedExceptionHandler(exception);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    void testIllegalArgumentExceptionHandler_ShouldReturnBadRequest_ForIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid input");

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.illegalArgumentExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getBody().error());
        assertEquals("Invalid input", response.getBody().message());
    }

    @Test
    void testIllegalArgumentExceptionHandler_ShouldReturnBadRequest_ForMissingParametersException() {
        OperationParameter operationParameter = mock(OperationParameter.class);
        Set<OperationParameter> missingParameters = Set.of(operationParameter);

        MissingParametersException exception = new MissingParametersException(missingParameters);

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.illegalArgumentExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getBody().error());
    }

    @Test
    void testIllegalArgumentExceptionHandler_ShouldReturnBadRequest_ForInvalidAmountException() {
        BusinessException exception = new InvalidAmountException(invalidAmountExceptionMessage, INVALID_ATTRIBUTE);

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.illegalArgumentExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getBody().error());
        assertEquals(invalidAmountExceptionMessage, response.getBody().message());
    }

    @Test
    void testIllegalArgumentExceptionHandler_ShouldReturnBadRequest_ForInsufficientFundsException() {
        BusinessException exception = new InsufficientFundsException(insufficientFundsExceptionMessage, INSUFFICIENT_BALANCE);

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.illegalArgumentExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getBody().error());
        assertEquals(insufficientFundsExceptionMessage, response.getBody().message());
    }

    @Test
    void testResourceJpaExceptionHandler_ForNotFound() {
        ResourceJpaException exception = mock(ResourceJpaException.class);
        when(exception.getErrorType()).thenReturn(BusinessErrorType.NOT_FOUND);
        when(exception.getLocalizedMessage()).thenReturn("Resource not found");

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.resourceExceptionHandler(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(response.getBody().error(), ErrorType.NOT_FOUND);
    }

    @Test
    void testResourceJpaExceptionHandler_ForInternalError() {
        ResourceJpaException exception = mock(ResourceJpaException.class);
        when(exception.getErrorType()).thenReturn(BusinessErrorType.INTERNAL_ERROR);
        when(exception.getLocalizedMessage()).thenReturn("Resource not found");

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.resourceExceptionHandler(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(response.getBody().error(), ErrorType.INTERNAL_ERROR);
    }

    @Test
    void testRuntimeExceptionHandler() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        ResponseEntity<ExceptionResponseDto> response = globalExceptionHandler.exceptionHandler(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
