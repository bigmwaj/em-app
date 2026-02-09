package ca.bigmwaj.emapp.as.api.shared.search;

import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortByClausePatternsValidator implements ConstraintValidator<ValidSortByClausePatterns, List<SortByClause>> {

    private ValidSortByClausePatterns validSortByPatterns;

    @Override
    public void initialize(ValidSortByClausePatterns constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.validSortByPatterns = constraintAnnotation;
    }

    @Override
    public boolean isValid(List<SortByClause> sortByItems, ConstraintValidatorContext context) {
        if (sortByItems == null || sortByItems.isEmpty()) {
            return true;
        }

        sortByItems.stream()
                .filter(SortByClause::isValid)
                .forEach(this::validate);

        var errorMessages = sortByItems.stream()
                .map(SortByClause::getValidationErrorMessages)
                .flatMap(List::stream)
                .collect(Collectors.joining(","));

        if (!errorMessages.isEmpty()) {
            context.buildConstraintViolationWithTemplate(errorMessages)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private void validate(SortByClause sortBy) {
        if (sortBy.isNotValid()) {
            return;
        }

        validateFieldName(sortBy);
    }

    private void validateFieldName(SortByClause sortBy) {
        String fieldName = sortBy.getName();

        if (fieldName == null) {
            sortBy.addMessage(MessageConstants.MSG0001);
            return;
        }

        boolean isValid = getSupportedFieldNames().stream()
                .anyMatch(fieldName::equals);

        if (!isValid) {
            sortBy.addMessage(String.format(MessageConstants.MSG0006, fieldName));
        }
    }

    List<String> getSupportedFieldNames() {
        return Arrays.stream(validSortByPatterns.supportedFields()).map(SortByClauseSupportedField::name).toList();
    }
}
