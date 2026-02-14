package ca.bigmwaj.emapp.as.validator.rule.platform;

import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

@Component("BirthDateRule")
public class BirthDateRule extends AbstractRule {

    @Override
    public boolean isValid(Object birthDate, Map<String, String> parameters) {
        if (birthDate == null) {
            return true; // Let @NotNull handle null validation if required
        }

        LocalDate _birthDate = (LocalDate) birthDate;
        LocalDate today = LocalDate.now();
        Period age = Period.between(_birthDate, today);

        int minAge;
        if (parameters.containsKey("minAge")) {
            minAge = Integer.parseInt(parameters.get("minAge"));
        } else {
            throw new ValidationConfigurationException("BirthDateRule requires a minAge parameter");
        }

        return age.getYears() >= minAge;
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        int minAge = Integer.parseInt(parameters.get("minAge"));
        return String.format("The %s indicates an age that is less than the required minimum of %d years.", fieldName, minAge);
    }
}
