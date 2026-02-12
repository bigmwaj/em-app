package ca.bigmwaj.emapp.as.validator.platform;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, LocalDate> {

    private int minAge;

    @Override
    public void initialize(ValidBirthDate constraintAnnotation) {
        this.minAge = constraintAnnotation.minAge();
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true; // Let @NotNull handle null validation if required
        }

        LocalDate today = LocalDate.now();
        Period age = Period.between(birthDate, today);
        
        return age.getYears() >= minAge;
    }
}
