package ca.bigmwaj.emapp.as.api.shared;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FilterPatternsValidator.class) // Link to the validator class
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFilterPatterns {
    String message() default "Invalid request filter!"; // Default error message

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    FilterSupportedField[] supportedFields() default {};
}

