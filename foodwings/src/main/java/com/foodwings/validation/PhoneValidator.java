package com.foodwings.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Implementation of the {@link Phone} constraint.
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null / blank is considered valid; combine with @NotBlank when the field is required
        if (value == null || value.isBlank()) {
            return true;
        }
        return PHONE_PATTERN.matcher(value).matches();
    }
}
