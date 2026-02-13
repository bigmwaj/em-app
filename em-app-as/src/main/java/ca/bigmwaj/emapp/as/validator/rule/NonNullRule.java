package ca.bigmwaj.emapp.as.validator.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NonNullRule extends AbstractRule{

    @Override
    public boolean isValid(Object value) {
        return value != null;
    }
}
