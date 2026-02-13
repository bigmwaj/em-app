package ca.bigmwaj.emapp.as.validator.shared;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SpringDtoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDto {
    String message() default "This DTO is not valid!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The namespace of the Spring validator to be used for validating a given DTO.
     */
    String value();
}
