package ca.bigmwaj.emapp.as.api.shared;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FilterPatternsValidator.class) // Link to the validator class
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterSupportedField {
    String name();

    Class<?> type();

    String rootEntityName() default "";

    String entityFieldName() default "";
}
