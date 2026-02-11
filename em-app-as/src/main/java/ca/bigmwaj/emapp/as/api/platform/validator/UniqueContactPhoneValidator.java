package ca.bigmwaj.emapp.as.api.platform.validator;

import ca.bigmwaj.emapp.as.dto.platform.ContactPhoneDto;
import ca.bigmwaj.emapp.as.service.platform.ContactPhoneService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueContactPhoneValidator implements ConstraintValidator<UniqueContactPhone, ContactPhoneDto> {

    @Autowired
    private ContactPhoneService contactPhoneService;

    @Override
    public void initialize(UniqueContactPhone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ContactPhoneDto phoneDto, ConstraintValidatorContext context) {
        if (phoneDto == null || phoneDto.getPhone() == null || phoneDto.getHolderType() == null) {
            return true; // Let other validators handle null checks
        }

        return contactPhoneService.isPhoneUnique(phoneDto.getHolderType(), phoneDto.getPhone());
    }
}
