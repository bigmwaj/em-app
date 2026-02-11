package ca.bigmwaj.emapp.as.api.platform.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueContactEmailValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueContactEmail {
    String message() default "Email with this holder type already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
