package ca.bigmwaj.emapp.as.validator.shared;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WhereClausePatternsValidator.class) // Link to the validator class
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface WhereClauseSupportedField {
    String name();

    Class<?> type();

    String rootEntityName() default "";

    String entityFieldName() default "";
}
