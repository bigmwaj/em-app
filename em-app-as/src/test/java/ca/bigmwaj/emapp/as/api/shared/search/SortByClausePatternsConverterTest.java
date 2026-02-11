package ca.bigmwaj.emapp.as.api.shared.search;

import ca.bigmwaj.emapp.as.api.shared.converter.SortByClausePatternsConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SortByClausePatternsConverterTest {

    @Test
    void givenNullPatterns_whenConvert_thenReturnsEmptyList() {
        var converter = new SortByClausePatternsConverter(null, null);
        var result = converter.convert();
        assertNotNull(result, "convert() should not return null");
        assertTrue(result.isEmpty(), "convert() should return an empty list when patterns is null");
    }

    @Test
    void givenNullTargetType_whenConvert_thenReturnsEmptyList() {
        var converter = new SortByClausePatternsConverter(null, "anyPattern");
        var result = converter.convert();
        assertNotNull(result, "convert() should not return null");
        assertTrue(result.isEmpty(), "convert() should return an empty list when patterns is null");
    }

    @Test
    void givenNullPatterns_whenConvert_thenReturnsEmptyList2() {
        // Given
        var patterns = "field1;field2";
        var targetType = TypeDescriptor.valueOf(Void.class);
        var converter = new SortByClausePatternsConverter(targetType, patterns);
        //var spyConverter = spy(converter);

        // Stub the method on the spy
        //doReturn(mockedMap).when(spyConverter).fetchSupportedRootEntityName(any(TypeDescriptor.class));

        // When
        var result = converter.convert();

        // Then
        assertEquals(2, result.size());
    }
}
