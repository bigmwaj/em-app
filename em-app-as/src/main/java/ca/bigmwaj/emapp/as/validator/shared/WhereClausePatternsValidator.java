package ca.bigmwaj.emapp.as.validator.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WhereClausePatternsValidator implements ConstraintValidator<ValidWhereClausePatterns, List<WhereClause>> {

    private ValidWhereClausePatterns patterns;

    @Override
    public void initialize(ValidWhereClausePatterns constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.patterns = constraintAnnotation;
    }

    @Override
    public boolean isValid(List<WhereClause> whereClauses, ConstraintValidatorContext context) {
        if (whereClauses == null || whereClauses.isEmpty()) {
            return true;
        }

        whereClauses.stream()
                .filter(WhereClause::isValid)
                .forEach(this::validate);

        var errorMessages = whereClauses.stream()
                .map(WhereClause::getValidationErrorMessages)
                .flatMap(List::stream)
                .collect(Collectors.joining(","));

        if (!errorMessages.isEmpty()) {
            context.buildConstraintViolationWithTemplate(errorMessages)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private void validate(WhereClause whereClause) {
        if (whereClause.isNotValid()) {
            return;
        }

        validateFieldName(whereClause);
        validateOperator(whereClause);
        validateValues(whereClause);
    }

    private void validateFieldName(WhereClause whereClause) {
        String fieldName = whereClause.getName();

        if (fieldName == null) {
            whereClause.addMessage(MessageConstants.MSG0001);
            return;
        }

        boolean isValid = getSupportedFieldNames().stream()
                .anyMatch(fieldName::equals);

        if (!isValid) {
            whereClause.addMessage(
                    String.format(MessageConstants.MSG0006, fieldName)
            );
        }
    }

    private void validateOperator(WhereClause whereClause) {
        if (whereClause.getOper() == null) {
            whereClause.addMessage(MessageConstants.MSG0007);
        }
    }

    private void validateValues(WhereClause whereClause) {
        var values = whereClause.getValues();

        if (values == null) {
            whereClause.addMessage(MessageConstants.MSG0008);
            return;
        }

        String message = switch (whereClause.getOper()) {
            case like, eq, ne, lt, lte, gt, gte -> values.size() == 1 ? null : MessageConstants.MSG0009;
            case in, ni -> !values.isEmpty() ? null : MessageConstants.MSG0010;
            case btw -> values.size() == 2 ? null : MessageConstants.MSG0011;
        };

        if (message != null) {
            whereClause.addMessage(message);
        }
    }

    public List<String> getSupportedFieldNames() {
        return Arrays.stream(patterns.supportedFields()).map(WhereClauseSupportedField::name).toList();
    }
}
