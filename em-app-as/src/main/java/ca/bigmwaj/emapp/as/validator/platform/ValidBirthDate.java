package ca.bigmwaj.emapp.as.validator.platform;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BirthDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBirthDate {
    String message() default "Age must be at least 18 years";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    int minAge() default 18;
}
