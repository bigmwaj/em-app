package ca.bigmwaj.emapp.as.api.shared.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SortByClausePatternsValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SortByClauseSupportedField {
    String name();

    String rootEntityName() default "";

    String entityFieldName() default "";
}
