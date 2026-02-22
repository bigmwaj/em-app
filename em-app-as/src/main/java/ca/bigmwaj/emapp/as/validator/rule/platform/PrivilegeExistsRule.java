package ca.bigmwaj.emapp.as.validator.rule.platform;

import ca.bigmwaj.emapp.as.dao.platform.PrivilegeDao;
import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("PrivilegeExistsRule")
public class PrivilegeExistsRule extends AbstractRule {

    @Autowired
    private PrivilegeDao privilegeDao;

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if( value == null ){
            return true; // Let @NotNull handle this
        }
        try {
            Short id = Short.valueOf(value.toString());
            return privilegeDao.existsById(id);
        } catch (NumberFormatException e) {
            throw new ValidationConfigurationException(e.getMessage(), e);
        }
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return "Privilege with ID %s does not exist.".formatted(value.toString());
    }
}
