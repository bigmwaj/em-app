package ca.bigmwaj.emapp.as.validator.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Component("MaxLengthRule")
public class MaxLengthRule extends AbstractRule {

    private int maxLength;

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true; // Let @NotNull handle this
        }
        if (value instanceof String) {
            return ((String) value).length() <= maxLength;
        }
        return false;
    }
}
