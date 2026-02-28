package ca.bigmwaj.emapp.as.validator.rule.platform;

import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import ca.bigmwaj.emapp.as.lvo.platform.AccountContactRoleLvo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("PrincipalAccountContactRule")
public class PrincipalAccountContactRule extends AbstractRule {

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if (value == null) { // The NotNull rule should handle this case
            return true;
        }

        if (value instanceof List<?> accountContacts) {
            if (accountContacts.isEmpty()) {
                return true; // The NotEmpty rule should handle this case
            }

            var primaryAccountContact = accountContacts.get(0);

            if (primaryAccountContact instanceof AccountContactDto acr) {
                return AccountContactRoleLvo.PRINCIPAL.equals(acr.getRole());
            }

        }
        throw new ValidationConfigurationException("PrincipalAccountContactRule can only be applied to List<AccountContactDto> fields.");
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return "The first contact in the '%s' list must have the role of 'PRINCIPAL'".formatted(fieldName);
    }
}
