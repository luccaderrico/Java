package com.services.wallet.resources.exceptions;

import com.services.wallet.domain.exceptions.BusinessErrorType;
import com.services.wallet.domain.exceptions.ResourceException;
import org.springframework.http.*;

public class ResourceJpaException extends ResourceException {

    public ResourceJpaException(String message) {
        super(message);
    }
    public ResourceJpaException(String message, HttpStatus httpStatus, BusinessErrorType errorType) {
        super(message, httpStatus, errorType);
    }
}
