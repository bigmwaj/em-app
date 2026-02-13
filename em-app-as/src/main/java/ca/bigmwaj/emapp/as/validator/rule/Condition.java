package ca.bigmwaj.emapp.as.validator.rule;

import jakarta.validation.ConstraintValidatorContext;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(setterPrefix = "with")
public class Condition {
    private boolean condition;

    @Singular
    private List<AbstractRule> rules;

    public void validate(ConstraintValidatorContext context, Object dto, String fieldName) {
        if (condition) {
            for (AbstractRule rule : rules) {
                rule.validate(context, dto, fieldName);
            }
        }
    }
}
