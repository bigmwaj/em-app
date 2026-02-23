package ca.bigmwaj.emapp.as.validator.rule.platform;

import ca.bigmwaj.emapp.as.dao.platform.RoleDao;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("RoleExistsRule")
public class RoleExistsRule extends AbstractRule {

    @Autowired
    private RoleDao roleDao;

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if( value == null ){
            return true; // Let @NotNull handle this
        }
        try {
            Short id = Short.valueOf(value.toString());
            return roleDao.existsById(id);
        } catch (NumberFormatException e) {
            throw new ValidationConfigurationException(e.getMessage(), e);
        }
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return "Role with ID %s does not exist.".formatted(value.toString());
    }
}
