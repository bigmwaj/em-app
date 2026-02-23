package ca.bigmwaj.emapp.as.validator.rule.platform;

import ca.bigmwaj.emapp.as.dao.platform.GroupDao;
import ca.bigmwaj.emapp.as.validator.rule.common.AbstractRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("GroupNameUniqueRule")
public class GroupNameUniqueRule extends AbstractRule {

    @Autowired
    private GroupDao GroupDao;

    @Override
    public boolean isValid(Object value, Map<String, String> parameters) {
        if (value == null) {
            return true; // Let @NotNull handle this
        }
        return !GroupDao.existsByNameIgnoreCase(value.toString());
    }

    @Override
    public String getErrorMessage(String fieldName, Object value, Map<String, String> parameters) {
        return "The group '%s' already exists." .formatted(value.toString());
    }
}
