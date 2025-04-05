package com.services.wallet.application.web.validator;

import com.services.wallet.application.web.utils.DocumentNumberValidator;
import jakarta.validation.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DocumentNumberValidatorTest {

    private DocumentNumberValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DocumentNumberValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void testIsValid_NullValue_ShouldReturnFalse() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void testIsValid_EmptyString_ShouldReturnFalse() {
        assertFalse(validator.isValid("", context));
    }

    @Test
    void testIsValid_InvalidLength_ShouldReturnFalse() {
        assertFalse(validator.isValid("123", context));
        assertFalse(validator.isValid("123456789012345", context));
    }

    @Test
    void testIsValid_ValidCPF_ShouldReturnTrue() {
        assertTrue(validator.isValid("697.110.370-09", context));
        assertTrue(validator.isValid("69711037009", context));
    }

    @Test
    void testIsValid_InvalidCPF_ShouldReturnFalse() {
        assertFalse(validator.isValid("111.111.111-11", context));
        assertFalse(validator.isValid("11111111111", context));
    }

    @Test
    void testIsValid_ValidCNPJ_ShouldReturnTrue() {
        assertTrue(validator.isValid("91.840.515/0001-55", context));
        assertTrue(validator.isValid("91840515000155", context));
    }

    @Test
    void testIsValid_InvalidCNPJ_ShouldReturnFalse() {
        assertFalse(validator.isValid("11.111.111/0001-11", context));
        assertFalse(validator.isValid("11111111000111", context));
    }

    @Test
    void testIsValid_InvalidStateException_ShouldReturnFalse() {
        assertFalse(validator.isValid("00000000000", context));
        assertFalse(validator.isValid("00000000000000", context));
    }
}
