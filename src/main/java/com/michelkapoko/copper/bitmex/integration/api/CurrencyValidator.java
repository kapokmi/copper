package com.michelkapoko.copper.bitmex.integration.api;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator mostly use in the withdrawalRequest to make sure that the request is made for a specific currency
 */
public class CurrencyValidator implements ConstraintValidator<ValidCurrency, Currency> {
    @Override
    public void initialize(ValidCurrency constraintAnnotation) {
    }

    @Override
    public boolean isValid(Currency value, ConstraintValidatorContext context) {
        return value != null && value != Currency.all;
    }
}
