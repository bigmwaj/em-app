package ca.bigmwaj.emapp.as.validator.shared;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotNullOnCreateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNotNullOnCreate {
    String message() default "This field should be not null when creating a new entity!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    int minAge() default 18;
}
