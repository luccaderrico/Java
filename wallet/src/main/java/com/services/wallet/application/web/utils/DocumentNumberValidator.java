package com.services.wallet.application.web.utils;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import com.services.wallet.application.web.annotations.CPF_CNPJ;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DocumentNumberValidator implements ConstraintValidator<CPF_CNPJ, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return false;

        value = formatDocumentNumber(value);

        if (!value.matches("\\d+")) return false;

        try {
            if (value.length() == 11) {
                if (isInvalidCPF(value)) return false;
                return new CPFValidator(false).isEligible(value);
            } else if (value.length() == 14) {
                if (isInvalidCNPJ(value)) return false;
                return new CNPJValidator(false).isEligible(value);
            }
        } catch (InvalidStateException e) {
            return false;
        }

        return false;
    }

    public static String formatDocumentNumber(String value) {
        return value.replaceAll("[^0-9]", "");
    }

    private boolean isInvalidCNPJ(String value) {
        List<String> invalidCNPJs = List.of(
            "11111111000111", "00000000000000", "12345678000195" // Add any other invalid patterns
        );
        return invalidCNPJs.contains(value);
    }

    private boolean isInvalidCPF(String value) {
        List<String> invalidCPFs = List.of(
                "11111111111", "00000000000"// Add any other invalid patterns
        );
        return invalidCPFs.contains(value);
    }
}