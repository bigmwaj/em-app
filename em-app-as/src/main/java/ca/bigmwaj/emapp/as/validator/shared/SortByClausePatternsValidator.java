package ca.bigmwaj.emapp.as.validator.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortByClausePatternsValidator implements ConstraintValidator<ValidSortByClausePatterns, List<SortByClause>> {

    private ValidSortByClausePatterns patterns;

    @Override
    public void initialize(ValidSortByClausePatterns constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.patterns = constraintAnnotation;
    }

    @Override
    public boolean isValid(List<SortByClause> sortByClauses, ConstraintValidatorContext context) {
        if (sortByClauses == null || sortByClauses.isEmpty()) {
            return true;
        }

        sortByClauses.stream()
                .filter(SortByClause::isValid)
                .forEach(this::validate);

        var errorMessages = sortByClauses.stream()
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

    private void validate(SortByClause sortByClause) {
        if (sortByClause.isNotValid()) {
            return;
        }

        validateFieldName(sortByClause);
    }

    private void validateFieldName(SortByClause sortByClause) {
        String fieldName = sortByClause.getName();

        if (fieldName == null) {
            sortByClause.addMessage(MessageConstants.MSG0001);
            return;
        }

        boolean isValid = getSupportedFieldNames().stream()
                .anyMatch(fieldName::equals);

        if (!isValid) {
            sortByClause.addMessage(String.format(MessageConstants.MSG0006, fieldName));
        }
    }

    public List<String> getSupportedFieldNames() {
        return Arrays.stream(patterns.supportedFields()).map(SortByClauseSupportedField::name).toList();
    }
}
