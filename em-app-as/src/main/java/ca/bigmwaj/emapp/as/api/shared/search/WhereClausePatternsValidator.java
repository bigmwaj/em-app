package ca.bigmwaj.emapp.as.api.shared.search;

import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WhereClausePatternsValidator implements ConstraintValidator<ValidWhereClausePatterns, List<WhereClause>> {

    private ValidWhereClausePatterns validFilterByPatterns;

    @Override
    public void initialize(ValidWhereClausePatterns constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.validFilterByPatterns = constraintAnnotation;
    }

    @Override
    public boolean isValid(List<WhereClause> filterByItems, ConstraintValidatorContext context) {
        if (filterByItems == null || filterByItems.isEmpty()) {
            return true;
        }

        filterByItems.stream()
                .filter(WhereClause::isValid)
                .forEach(this::validate);

        var errorMessages = filterByItems.stream()
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

    private void validate(WhereClause filterBy) {
        if (filterBy.isNotValid()) {
            return;
        }

        validateFieldName(filterBy);
        validateOperator(filterBy);
        validateValues(filterBy);
    }

    private void validateFieldName(WhereClause filterBy) {
        String fieldName = filterBy.getName();

        if (fieldName == null) {
            filterBy.addMessage(MessageConstants.MSG0001);
            return;
        }

        boolean isValid = getSupportedFieldNames().stream()
                .anyMatch(fieldName::equals);

        if (!isValid) {
            filterBy.addMessage(
                    String.format(MessageConstants.MSG0006, fieldName)
            );
        }
    }

    private void validateOperator(WhereClause filterBy) {
        if (filterBy.getOper() == null) {
            filterBy.addMessage(MessageConstants.MSG0007);
        }
    }

    private void validateValues(WhereClause filterBy) {
        var values = filterBy.getValues();

        if (values == null) {
            filterBy.addMessage(MessageConstants.MSG0008);
            return;
        }

        String message = switch (filterBy.getOper()) {
            case like, eq, ne, lt, lte, gt, gte -> values.size() == 1 ? null : MessageConstants.MSG0009;
            case in, ni -> !values.isEmpty() ? null : MessageConstants.MSG0010;
            case btw -> values.size() == 2 ? null : MessageConstants.MSG0011;
        };

        if (message != null) {
            filterBy.addMessage(message);
        }
    }

    List<String> getSupportedFieldNames() {
        return Arrays.stream(validFilterByPatterns.supportedFields()).map(WhereClauseSupportedField::name).toList();
    }
}
