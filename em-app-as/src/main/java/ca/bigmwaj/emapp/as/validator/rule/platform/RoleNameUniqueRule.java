package ca.bigmwaj.emapp.as.validator.rule.platform;

import ca.bigmwaj.emapp.as.dao.platform.RoleDao;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("RoleNameUniqueRule")
public class RoleNameUniqueRule extends AbstractRule {

    @Autowired
    private RoleDao roleDao;

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if (value == null) {
            return true; // Let @NotNull handle this
        }
        return !roleDao.existsByNameIgnoreCase(value.toString());
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return "The rule '%s' is already." .formatted(value.toString());
    }
}
