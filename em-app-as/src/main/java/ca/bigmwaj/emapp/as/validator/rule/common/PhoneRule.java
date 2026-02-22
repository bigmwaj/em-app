package ca.bigmwaj.emapp.as.validator.rule.common;

import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactPhoneDto;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

@Component("PhoneRule")
public class PhoneRule extends AbstractRule {

    private static final String INDICATIVE_US = "+1";
    private static final String ERROR_MSG_PHONE_VALIDATION = "The field '%s' must be a valid phone.";
    private static final String ERROR_MSG_INDICATIVE_NOT_SUPPORTED = "The indicative '%s' is not supported by PhoneRule yet";
    private static final String ERROR_MSG_PHONE_FORMAT_US = "phone should be 10 digits for North America";

    private static final Pattern PHONE_PATTERN_US = Pattern.compile("^\\d{10}$");

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        return true;
    }

    @Override
    public boolean isValid(Object dto, Object value, Map<String, String> parameters) {
        if (value == null) {
            return true;
        }
        String phone = value.toString();
        String indicative = null;
        if (dto instanceof ContactPhoneDto contact) {
            indicative = contact.getIndicative();
        }

        if (dto instanceof AccountDto account) {
            indicative = account.getAdminUsernameTypePhoneIndicative();
        }

        if (indicative == null) {
            return true; // if indicative is not provided, we cannot validate the phone, so we consider it valid. The validation will be triggered when indicative is provided.
        }

        if (INDICATIVE_US.equals(indicative)) {
            // phone should be 10 digits for North America
            boolean valid = PHONE_PATTERN_US.matcher(phone).matches();
            if (!valid) {
                parameters.put("message", ERROR_MSG_PHONE_FORMAT_US);
            }
            return valid;
        } else {
            throw new ValidationConfigurationException(ERROR_MSG_INDICATIVE_NOT_SUPPORTED.formatted(indicative));
        }
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        if (parameters != null && parameters.containsKey("message")) {
            return parameters.get("message");
        } else {
            return ERROR_MSG_PHONE_VALIDATION.formatted(fieldName);
        }
    }
}
