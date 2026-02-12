package ca.bigmwaj.emapp.as.converter.shared;

import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.validator.shared.SortByClausePatternsValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class SortByClausePatternsValidatorTest {
    @Test
    void givenWellMappedPatterns_whenIsValid_thenReturnsTrue() {
        // Given
        var supportedFieldNames = List.of("field1", "field2");
        var validator = new SortByClausePatternsValidator();
        var items = List.of(new SortByClause("field1"), new SortByClause("field2", SortByClause.sortType.asc));
        var spyValidator = spy(validator);
        doReturn(supportedFieldNames).when(spyValidator).getSupportedFieldNames();

        // When
        var isValid = spyValidator.isValid(items, null);

        // Then
        assertTrue(isValid);
    }

    @Test
    void givenNotWellMappedPatterns_whenIsValid_thenReturnsTrue() {
        // Given
        var supportedFieldNames = List.of("field1", "field3");
        var validator = new SortByClausePatternsValidator();
        var items = List.of(new SortByClause("field1"), new SortByClause("field2", SortByClause.sortType.asc));
        var spyValidator = spy(validator);
        var context = new MockConstraintValidatorContext();
        doReturn(supportedFieldNames).when(spyValidator).getSupportedFieldNames();

        // When
        var isValid = spyValidator.isValid(items, new MockConstraintValidatorContext());

        // Then
        assertFalse(isValid);
    }
}
