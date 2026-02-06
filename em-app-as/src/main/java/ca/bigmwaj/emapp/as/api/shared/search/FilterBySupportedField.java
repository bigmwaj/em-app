package ca.bigmwaj.emapp.as.api.shared.search;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FilterByPatternsValidator.class) // Link to the validator class
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterBySupportedField {
    String name();

    Class<?> type();

    String rootEntityName() default "";

    String entityFieldName() default "";
}
