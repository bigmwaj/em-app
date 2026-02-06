package ca.bigmwaj.emapp.as.api.shared.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterByPatternsConverterTest {

    @Test
    void givenNullTargetType_whenConvert_thenReturnsEmptyList() {
        var converter = new FilterByPatternsConverter(null, null);
        var result = converter.convert();
        assertNotNull(result, "convert() should not return null");
        assertTrue(result.isEmpty(), "convert() should return an empty list when patterns is null");
    }
}
