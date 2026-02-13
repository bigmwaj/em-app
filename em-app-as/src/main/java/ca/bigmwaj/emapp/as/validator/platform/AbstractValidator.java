package ca.bigmwaj.emapp.as.validator.platform;

import jakarta.validation.ConstraintValidator;

import java.lang.annotation.Annotation;

public abstract class AbstractValidator<V extends Annotation, DTO> implements ConstraintValidator<V, DTO> {

}
