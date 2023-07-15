package com.example.dynamic.contraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ScoreRangeValidator implements ConstraintValidator<ValidScoreRange, String> {
    @Override
    public void initialize(ValidScoreRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !value.matches("^\\d+-\\d+$")) {
            return false;
        }

        String[] parts = value.split("-");
        int start = Integer.parseInt(parts[0]);
        int end = Integer.parseInt(parts[1]);

        return start >= 1 && start <= 100 && end >= 1 && end <= 100;
    }
}

