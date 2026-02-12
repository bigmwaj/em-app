package ca.bigmwaj.emapp.as.validator.shared;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotEmptyOnCreateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNotEmptyOnCreate {
    String message() default "This collection should be not empty when creating a new entity!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    int minAge() default 18;
}
