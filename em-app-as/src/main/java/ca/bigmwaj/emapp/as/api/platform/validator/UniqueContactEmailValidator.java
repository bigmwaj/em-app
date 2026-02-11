package ca.bigmwaj.emapp.as.api.platform.validator;

import ca.bigmwaj.emapp.as.dto.platform.ContactEmailDto;
import ca.bigmwaj.emapp.as.service.platform.ContactEmailService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueContactEmailValidator implements ConstraintValidator<UniqueContactEmail, ContactEmailDto> {

    @Autowired
    private ContactEmailService contactEmailService;

    @Override
    public void initialize(UniqueContactEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ContactEmailDto emailDto, ConstraintValidatorContext context) {
        if (emailDto == null || emailDto.getEmail() == null || emailDto.getHolderType() == null) {
            return true; // Let other validators handle null checks
        }

        return contactEmailService.isEmailUnique(emailDto.getHolderType(), emailDto.getEmail());
    }
}
