package ca.bigmwaj.emapp.as.validator.rule.platform;

import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.service.platform.UserService;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("UserExistsRule")
public class UserExistsRule extends AbstractRule {

    @Autowired
    private UserDao userDao;

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if( value == null ){
            return true; // Let @NotNull handle this
        }
        try {
            Short id = Short.valueOf(value.toString());
            return userDao.existsById(id);
        } catch (NumberFormatException e) {
            throw new ValidationConfigurationException(e.getMessage(), e);
        }
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return "User with ID %s does not exist.".formatted(value.toString());
    }
}
