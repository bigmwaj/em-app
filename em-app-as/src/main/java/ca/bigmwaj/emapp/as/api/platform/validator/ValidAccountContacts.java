package ca.bigmwaj.emapp.as.api.platform.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AccountContactsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccountContacts {
    String message() default "Invalid account contacts";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
