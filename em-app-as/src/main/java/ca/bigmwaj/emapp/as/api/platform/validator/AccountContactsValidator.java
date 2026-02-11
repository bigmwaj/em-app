package ca.bigmwaj.emapp.as.api.platform.validator;

import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountContactRoleLvo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class AccountContactsValidator implements ConstraintValidator<ValidAccountContacts, List<AccountContactDto>> {

    @Override
    public void initialize(ValidAccountContacts constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<AccountContactDto> accountContacts, ConstraintValidatorContext context) {
        if (accountContacts == null || accountContacts.isEmpty()) {
            return true; // @NotEmpty will handle this
        }

        // If only one contact, it must be PRINCIPAL
        if (accountContacts.size() == 1) {
            AccountContactDto contact = accountContacts.get(0);
            if (contact.getRole() != AccountContactRoleLvo.PRINCIPAL) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "When there is only one account contact, the role must be PRINCIPAL"
                ).addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
