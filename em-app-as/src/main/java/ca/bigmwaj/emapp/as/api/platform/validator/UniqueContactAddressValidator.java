package ca.bigmwaj.emapp.as.api.platform.validator;

import ca.bigmwaj.emapp.as.dto.platform.ContactAddressDto;
import ca.bigmwaj.emapp.as.service.platform.ContactAddressService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueContactAddressValidator implements ConstraintValidator<UniqueContactAddress, ContactAddressDto> {

    @Autowired
    private ContactAddressService contactAddressService;

    @Override
    public void initialize(UniqueContactAddress constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ContactAddressDto addressDto, ConstraintValidatorContext context) {
        if (addressDto == null || addressDto.getAddress() == null || addressDto.getHolderType() == null) {
            return true; // Let other validators handle null checks
        }

        return contactAddressService.isAddressUnique(addressDto.getHolderType(), addressDto.getAddress());
    }
}
