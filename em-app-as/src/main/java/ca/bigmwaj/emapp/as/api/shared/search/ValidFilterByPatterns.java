package ca.bigmwaj.emapp.as.api.shared.search;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FilterByPatternsValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFilterByPatterns {
    String message() default "Invalid request filter!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    FilterBySupportedField[] supportedFields() default {};
}

