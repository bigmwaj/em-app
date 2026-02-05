package ca.bigmwaj.emapp.as.api.shared;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SortByPatternsValidator.class) // Link to the validator class
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortByPatterns {
    String message() default "Invalid request!"; // Default error message

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    SortBySupportedField[] supportedFields() default {};
}

