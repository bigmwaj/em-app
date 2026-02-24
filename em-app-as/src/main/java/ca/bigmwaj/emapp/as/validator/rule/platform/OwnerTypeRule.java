package ca.bigmwaj.emapp.as.validator.rule.platform;

import ca.bigmwaj.emapp.as.dto.platform.*;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Validates that the holder type is valid for the given DTO and its children.
 * This rule checks if the holder type is valid for the given DTO.
 * <ul>
 * <li>When the DTO is a ContactDto, it checks if the holder type is valid for the emails, phones and addresses
 * associated to the contact.</li>
 * <li>When the DTO is an AccountDto, it checks if the holder type is valid for the contact associated to the account.</li>
 * <li>When the DTO is a User, it checks if the holder type is valid for the contact associated to the account.</li>
 * <li>When the DTO is </li>
 * <li>When the DTO is </li>
 * </ul>
 */
@Component("OwnerTypeRule")
public class OwnerTypeRule extends AbstractRule {

    @Override
    public boolean isValid(Object ownerType, Map<String, String> parameters) {
        return true;
    }

    @Override
    public boolean isValid(Object dto, Object ownerType, Map<String, String> parameters) {
        Objects.requireNonNull(dto, "DTO cannot be null");
        if (ownerType == null) {
            return true;
        }

        if (dto instanceof AccountDto account) {
            if (account.getAccountContacts() == null) {
                return true;
            }

            return account.getAccountContacts().stream()
                    .map(AccountContactDto::getContact)
                    .filter(Objects::nonNull)
                    .map(ContactDto::getOwnerType)
                    .map(Objects::nonNull)
                    .allMatch(ownerType::equals);
        }

        if (dto instanceof ContactDto contact) {
            List<AbstractContactPointDto> contactPoints = new ArrayList<>();
            if (contact.getEmails() != null) {
                contactPoints.addAll(contact.getEmails());
            }
            if (contact.getPhones() != null) {
                contactPoints.addAll(contact.getPhones());
            }
            if (contact.getAddresses() != null) {
                contactPoints.addAll(contact.getAddresses());
            }

            return contactPoints.stream()
                    .map(AbstractContactPointDto::getOwnerType)
                    .filter(Objects::nonNull)
                    .allMatch(ownerType::equals);
        }

        if (dto instanceof UserDto user) {
            if (user.getContact() == null) {
                return true;
            }

            return Stream.of(user.getContact())
                    .map(ContactDto::getOwnerType)
                    .filter(Objects::nonNull)
                    .allMatch(ownerType::equals);
        }

        throw new ValidationConfigurationException("OwnerTypeRule is only applicable to AccountDto, UserDto and ContactDto");
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return "The holder type is invalid.";
    }
}
