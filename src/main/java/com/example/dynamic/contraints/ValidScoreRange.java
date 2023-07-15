package com.example.dynamic.contraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ScoreRangeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScoreRange {
    String message() default "Invalid score range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
